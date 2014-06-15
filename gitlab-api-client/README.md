# gitlab-api-client

A client for [GitLab][] REST API written in Java.

## License

> The MIT License (MIT)
>
> Copyright (c) 2014 Andreas Alanko, Emil Nilsson, Sony Mobile Communications AB.
> All rights reserved.
>
> Permission is hereby granted, free of charge, to any person obtaining a copy
> of this software and associated documentation files (the "Software"), to deal
> in the Software without restriction, including without limitation the rights
> to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
> copies of the Software, and to permit persons to whom the Software is
> furnished to do so, subject to the following conditions:
>
> The above copyright notice and this permission notice shall be included in
> all copies or substantial portions of the Software.
>
> THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
> IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
> FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
> AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
> LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
> OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
> THE SOFTWARE.

## Limitations

The client can currently only read data from the API and not make any changes.

## Installation

gitlab-api-client is simple to install with maven, just add the following lines to the project's `pom.xml` file:

    <dependencies>
        <dependency>
            <groupId>com.sonymobile.gitlab</groupId>
            <artifactId>gitlab-api-client</artifactId>
            <version>0.3</version>
        </dependency>
    </dependencies>

## Usage

An instance of `GitLabApiClient` must be created to connect to a server.
There are two ways to connect, either with a *private token* or with user credentials.

The constructor `GitLabApiClient(host, privateToken)` sets up the client using a private token:

    GitLabApiClient client = new GitLabApiClient("http://demo.gitlab.com", "Wvjy2Krpb7y8xi93owUz");

Use the static method `openSession(host, login, password)` to connect with user's login name and password:

    GitLabApiClient client = GitLabApiClient.openSession("http://demo.gitlab.com", "jsmith", "123456");

It is possible to temporarily assume the identity of another user with the method `asUser(userId)` using the
user ID of that user:

    // prints the name of the user with the provided user ID
    System.out.println(client.asUser(2).getCurrentUser().getName());
    // prints "John Smith"

Note that the client must be authenticated with the private token of an administrator user for this to work.

### Session

It is possible to explicitly fetch a session using `getSession(login, password)` method on an existing client as opposed
 to using the static `openSession(host, login, password)` method:

    // get a session for the user jsmith
    GitLabSessionInfo session = client.getSession("jsmith", "123456");

    // print the user's token
    System.out.println("Private token: " + session.getPrivateToken());
    // prints "Private token: Wvjy2Krpb7y8xi93owUz"

The session is fetched with a `POST` request to [/session][session].

### User

#### All users

The method `getUsers()` can be used to fetch all users:

    // print the name of all users
    for (final GitLabUserInfo user : client.getUsers()) {
        System.out.println(user.getName());
    }

The users are fetched with a `GET` request to [/users][listusers].

#### Single User by ID

To fetch just a single user by its user ID, use `getUser(userId)`:

    GitLabUserInfo user = client.getUser(1);

    // print the user's name
    System.out.println("User: " + user.getName());
    // prints "User: [username]

If the user doesn't exist `getUser(userId)` will return `null`.

The user is fetched with a `GET` request to [/users/:id][singleuser].

#### Current user

It is possible to retrieve the current user, e.g. the user the private token belongs to, using the `getCurrentUser()`
method:

    GitLabUserInfo user = client.getCurrentUser();

    // print the user's name
    System.out.println("Logged in user: " + user.getName());
    // prints "Logged in user: [username]

The current user is fetched with a `GET` request to [/user][currentuser].

### Groups

#### All groups

The method `getGroups()` can be used to fetch all groups accessible to the current user (administrators have access to
 all groups):

    // print the ID and name of all groups
    for (final GitLabGroupInfo group : client.getGroups()) {
        System.out.println(group.getID() + ": " + group.getName());
        // prints "1: [groupname]" and so on
    }

The groups are fetched with a `GET` request to [/groups][allgroups].

#### Single group by ID

The `getGroup(groupId)` method can be used to fetch a group by its group ID:

    GitLabGroupInfo group = client.getGroup(1);
    System.out.println(group.getID() + ": " + group.getName());
    // prints "1: [groupname]"

The group is fetched with a `GET` request to [/groups/:id][groupdetails].

### Group members

The method `getGroupMembers(groupId)` returns a list of all members of a group specified by its group ID:

    // prints the names of all members of the group with ID 1
    for (final GitLabGroupMemberInfo member : client.getGroupMembers(1)) {
        System.out.println(member.getName());
    }

The group members are fetched with a `GET` request to [/groups/:id/members][groupmembers].

### Proxy

A proxy can be configured either using the `GitLabApiClient` constructor or using [Java system properties][javaproxy].

The alternate constructor `GitLabApiClient(host, privateToken, proxyHost, proxyPort)` works as expected:

    GitLabApiClient client = new GitLabApiClient("http://demo.gitlab.com", "Wvjy2Krpb7y8xi93owUz", "proxyhost", 8080);

Or using the static `openSession(host, login, password, proxyHost, proxyPort)` method:

    GitLabApiClient client = new GitLabApiClient("http://demo.gitlab.com", "jsmith", "123456", "proxyhost", 8080);

The proxy server can also be set using `System.setProperty(key, value)`:

    System.setProperty("http.proxyHost", "proxyhost");
    System.setProperty("http.proxyPort", "8080");

This can also be done from the command line when running your application:

    java -Dhttp.proxyHost=proxyhost -Dhttp.proxyPort=8080 MyApplication

#### Proxy credentials

The proxy credentials can be set with the
`GitLabApiClient(host, privateToken, proxyHost, proxyPort, proxyUser, proxyPassword)` constructor:

    GitLabApiClient client = new GitLabApiClient("http://demo.gitlab.com", "Wvjy2Krpb7y8xi93owUz", "proxyhost", 8080,
        "username", "password");

The same constructor can be used with `null` for `proxyUser` and `proxyPassword` if credentials are not used.

#### Non-proxy hosts

Hosts to exclude from the proxy can be specified with the `GitLabApiClient(host, privateToken, proxyHost, proxyPort,
proxyUser, proxyPassword, excludedHostnames)` constructor where `excludedHostnames` is a `List` of hostname `Pattern`s
 to exclude:

    List<Pattern> nonProxyHosts = new ArrayList<Pattern>();
    nonProxyHosts.add(Pattern.compile("localhost"));
    nonProxyHosts.add(Pattern.compile("127\.0\.0\.[0-9]+"));
    nonProxyHosts.add(Pattern.compile("[^.]\.example\.net");

    GitLabApiClient client = new GitLabApiClient("http://demo.gitlab.com", "Wvjy2Krpb7y8xi93owUz", "proxyhost", 8080,
            "username", "password", nonProxyHosts);

Again, `proxyUser` and `proxyPassword` can be `null` if proxy credentials are not used.

[GitLab]:       https://www.gitlab.com/
[session]:      http://api.gitlab.org/session.html
[listusers]:    http://doc.gitlab.com/ce/api/users.html#list-users
[singleuser]:   http://doc.gitlab.com/ce/api/users.html#single-user
[currentuser]:  http://doc.gitlab.com/ce/api/users.html#current-user
[allgroups]:    http://doc.gitlab.com/ce/api/groups.html#list-project-groups
[groupdetails]: http://doc.gitlab.com/ce/api/groups.html#details-of-a-group
[groupmembers]: http://doc.gitlab.com/ce/api/groups.html#list-group-members
[javaproxy]:    http://docs.oracle.com/javase/6/docs/technotes/guides/net/proxies.html
