# tinyVPN

light VPN using SSH2 port forwarding

# Usage context

tinyVPN is made to redirect a port/list of port to a specific host via SSH.
The tool use a local port forwarding to a host, and redirect the port to another. 
Therefore, it is possible to chain a list of hosts to redirect.

For example, you need an access to a device Z. but the network only gives you an access
to the device X. and X has only an access to device Y.

The network will look like this :

```
LOCAL_HOST -> X -> Y -> Z
```

With tiny VPN you can redirect LOCAL_HOST directly to device Z by chaining the connections like this :

```
X connect to Y, redirected to localhost port 1
localhost port 1 connected to z, redirected to localhost port 2.
```

And to access it, you just need to start a connection to port 2.

# General usage

The tool can use CLI arguments or a script.

## CLI :

```
java -jar tinyVPN.jar [<USERNAME>:<PASSWORD>@<HOST>:<SSH_PORT>@<TARGET>:<PORT_TARGET>@<LOCAL_REDIRECTION>]
```

You can give as many connection you want. The SSH_PORT is 22 by default, if you do not provide it.

Also, in case of a chained connection, you can simplify the next argument, because tinyVPN will understand that 
you want to redirect the previous generated connection, to the next host. 

Example : 

```
java -jar tinyVPN.jar johndoe:password123@192.168.1.1@172.16.1.1@9000 potterharry:moldu@@www.facebook.com:80@9001
```

Here, you create a connection between 192.168.1.1:22 to 172.16.1.1:22 (both port 22). That connection is available from 127.0.0.1:9000

Then, you create a second connection using the previous one. Therefore, 127.0.0.1:9000(172.16.1.1:22) will redirect 127.0.0.1:9001 to facebook.com:80

Here is the summary :

```
before tinyVPN :
LOCAl_HOST -> 192.168.1.1 -> 172.16.1.1 -> FACEBOOK

After tinyVPN :
LOCALHOST (port 9001) -> FACEBOOK (port 80)
```

## SCRIPT

```
java -jar tinyVPN.jar -f <FILE_PATH>
```

The file takes the same argument than the CLI, but separated by a new line.

Example :

```
johndoe:password123@192.168.1.1@172.16.1.1@9000
potterharry:moldu@@www.facebook.com:80@9001
```

You can create as many connection you want. Refer to the CLI example for more information.

```
johndoe:password123@192.168.1.1@172.16.1.1@9000
potterharry:moldu@@www.facebook.com:80@9001
potterharry:moldu@127.0.0.1:9000@google.com:80@9004
potterharry:moldu@127.0.0.1:9000@youtube.com:80@9004
```

## SSH GUI

You can open an interactive SSH shell 

```
tiny VPN -g
```
