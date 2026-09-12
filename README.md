# ZeroBoard

ZeroBoard is a lightweight, real-time collaborative whiteboard for desktop Java. It combines a Swing client with a small multithreaded TCP server so multiple users can draw on the same canvas and exchange chat messages while connected to the same server.

## Features

- Real-time freehand drawing across connected clients
- Selectable brush color and brush size
- Whiteboard eraser
- Local clear and undo controls
- Collaborative text chat
- Connected-user list
- Configurable server IP address and port in the client
- No external libraries or build tools required

## How it works

ZeroBoard has two runtime components:

1. **Server** — `server.Server` listens on TCP port `5050`, accepts clients, and creates one `ClientHandler` thread per connection.
2. **Client** — `Main` starts the Swing application. Each client opens an `ObjectInputStream`/`ObjectOutputStream` connection to the server through `ClientNetwork`.

When a client connects, it first sends its username. After that, it can send serializable `Line` and `ChatMessage` objects:

- A drawn line is added immediately to the local canvas and relayed to every other connected client.
- A chat message is displayed locally and relayed to every other connected client.
- The server broadcasts the current list of usernames when someone joins or disconnects.
- Incoming network updates are applied on the Swing event thread so the UI remains responsive.

The server is a relay rather than a persistent collaboration service. It does not save drawings, replay previous lines to newly connected clients, or provide authentication.

## Project structure

```text
.
├── src/
│   ├── Main.java                         # Desktop client entry point
│   ├── client/
│   │   ├── connection/ClientNetwork.java # TCP client and message listener
│   │   ├── gui/                          # Swing panels and window
│   │   └── model/                        # Serializable Line and ChatMessage models
│   ├── commons/Message.java              # Generic message model (currently unused)
│   └── server/
│       ├── Server.java                    # TCP server entry point and broadcaster
│       └── ClientHandler.java             # Per-client stream and relay loop
├── .gitignore
└── zeroboardapplication.iml               # IntelliJ IDEA module metadata
```

## Requirements

- Java Development Kit (JDK) 8 or newer
- A graphical desktop environment for the Swing client
- Network access between the client machines and the server machine

Check your installation with:

```bash
java -version
javac -version
```

## Build

Run these commands from the repository root:

```bash
rm -rf out
mkdir out
javac -d out $(find src -name '*.java')
```

The compiled classes are written to `out/`, which is ignored by Git.

### Windows PowerShell

The equivalent PowerShell commands are:

```powershell
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
New-Item -ItemType Directory out
javac -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

## Run the application

### 1. Start the server

Start the server first:

```bash
java -cp out server.Server
```

The server listens on `0.0.0.0:5050` and prints connection and disconnection events to the terminal. Keep this process running while clients are using the whiteboard.

The port is currently defined as a constant in [`src/server/Server.java`](src/server/Server.java). To use a different port, update that constant and enter the same port in each client.

### 2. Start a client

In a separate terminal on the server machine, run:

```bash
java -cp out Main
```

To test collaboration locally, start the command more than once. Each invocation opens a separate client window.

When the window opens:

1. Enter the server IP address. Use `127.0.0.1` when the server is on the same machine.
2. Enter port `5050` unless you changed the server source.
3. Enter a username.
4. Select **Connect**.

For a client on another machine, replace `127.0.0.1` with the server machine's LAN IP address and allow inbound TCP traffic on port `5050` through the server machine's firewall.

## Using the client

### Whiteboard

- Drag the mouse over the white canvas to draw.
- **Color** opens a color chooser for subsequent strokes.
- **Brush** accepts a brush size from `1` through `50`.
- **Eraser** changes the brush color to white.
- **Clear** removes all lines from the current client's canvas after confirmation.
- **Undo** removes the most recently drawn local line.
- **Redo** is present in the interface but is not implemented yet.

Drawing changes are sent while the mouse is dragged. Other connected clients receive each line segment and render it on their canvases.

### Chat

Type a message in the chat field and select **Send**, or press Enter. Messages are sent with the configured username and appear in the chat area for all connected clients.

### Connection and users

The left panel shows the connection status and the usernames currently known by the server. Selecting **Disconnect** closes the socket and returns the client to a disconnected state.

## Network and data model

The application uses Java's built-in object serialization over TCP:

| Payload | Direction | Purpose |
| --- | --- | --- |
| `String` | Client → server | Initial username handshake |
| `Line` | Client → server → peers | One drawn line segment, including coordinates, color, and brush size |
| `ChatMessage` | Client → server → peers | Sender name and message text |
| `List<String>` | Server → clients | Current connected-user list |

The server deliberately excludes the originating client when relaying lines and chat messages because the originating client already updates its own UI.

## Troubleshooting

### `Connection refused`

- Confirm that `server.Server` is running.
- Check that the client IP and port match the server.
- If connecting across machines, use the server's LAN IP rather than `127.0.0.1`.
- Check the server firewall and confirm TCP port `5050` is allowed.

### The client window does not open

Run the client from a graphical desktop session. A headless environment cannot display the Swing UI.

### Drawing is not synchronized

Confirm that every client is connected to the same server instance and that the server terminal shows each connection. The current implementation only relays new line segments; it does not synchronize or persist the existing canvas for clients that join later.

## Current limitations and future improvements

- No persistent storage or session history
- No replay of existing drawings for newly connected clients
- Clear, undo, and redo are local operations; clear and undo are not broadcast
- Redo is currently a placeholder
- No authentication, authorization, encryption, or input-size limits
- The server port is configured in source code rather than through a command-line option
- The generic `commons.Message` class is currently unused

Potential next steps include adding a shared command protocol for canvas operations, replaying board state on connection, configurable server settings, TLS or authenticated sessions, and a proper build configuration.

## Development notes

This repository is intentionally small and uses only the Java standard library. IntelliJ IDEA can import `zeroboardapplication.iml` directly, or the project can be built from the command line using the commands above.

