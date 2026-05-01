# Minecraft Server Management Protocol Legacy Support

Adds a Minecraft Server Management Protocol endpoint to server software that does not provide Mojang's native implementation.

The Minecraft Server Management Protocol is the newer management API for Minecraft servers. It exposes server information and management actions through JSON-RPC over WebSocket, instead of relying on older mechanisms such as server-list ping or RCON. This plugin brings that protocol shape to older server versions and proxy environments so external tools can speak one management protocol across mixed infrastructure.

## What it provides

- A WebSocket endpoint for Minecraft Server Management Protocol clients.
- JSON-RPC request and response handling compatible with the protocol model documented on the Minecraft Wiki.
- Discovery through `rpc.discover`, so clients can inspect which methods are available.
- Server/proxy status support, including online player list and version information.
- Management operations for Bukkit-based servers, including players, allowlist, bans, operators, selected server settings, gamerules, save, stop, and system messages.
- Proxy management operations for Velocity and BungeeCord, including players, kick, status, stop, and system messages.
- Authentication by shared secret, with browser-origin checks for WebSocket subprotocol authentication.
- Optional TLS using a PKCS12 keystore.

Reference: https://minecraft.wiki/w/Minecraft_Server_Management_Protocol

## Platform Status

| Platform | Status |
| --- | --- |
| Spigot / Paper | Main supported runtime. Built against the Spigot 1.8.8 API for legacy compatibility. |
| Velocity | Proxy runtime support for discovery, players, kick, proxy status, proxy stop, system messages, and proxy/player notifications. |
| BungeeCord | Proxy runtime support for discovery, players, kick, proxy status, proxy stop, system messages, and proxy/player notifications. |

For legacy SpaceChunks usage, install the Spigot/Paper build on the backend server when you need live server status and online player counts from old Minecraft versions.

## Supported Methods

| Method group | Spigot / Paper | Velocity | BungeeCord |
| --- | --- | --- | --- |
| `rpc.discover` | yes | yes | yes |
| `minecraft:players` | yes | yes | yes |
| `minecraft:players/kick` | yes | yes | yes |
| `minecraft:server/status` | yes | yes, proxy status | yes, proxy status |
| `minecraft:server/stop` | yes | yes, stops proxy | yes, stops proxy |
| `minecraft:server/system_message` | yes | yes, proxy players | yes, proxy players |
| `minecraft:server/save` | yes | no | no |
| `minecraft:allowlist/*` | yes | no | no |
| `minecraft:bans/*` | yes | no | no |
| `minecraft:ip_bans/*` | yes | no | no |
| `minecraft:operators/*` | yes | no | no |
| `minecraft:serversettings/*` | partial | no | no |
| `minecraft:gamerules/*` | yes | no | no |

Spigot/Paper server settings are limited to APIs available through the Spigot 1.8.8 API. Implemented settings are `autosave`, `difficulty`, `use_allowlist`, `player_idle_timeout`, `allow_flight` read-only, `motd` read-only, `spawn_protection_radius`, `game_mode`, and `view_distance` read-only.

## Supported Notifications

| Notification group | Spigot / Paper | Velocity | BungeeCord |
| --- | --- | --- | --- |
| `minecraft:notification/server/started` | yes | yes | yes |
| `minecraft:notification/server/stopping` | yes | yes | yes |
| `minecraft:notification/server/status` | yes | yes | yes |
| `minecraft:notification/server/activity` | yes | yes | yes |
| `minecraft:notification/players/joined` | yes | yes | yes |
| `minecraft:notification/players/left` | yes | yes | yes |
| `minecraft:notification/server/saving` | yes | no | no |
| `minecraft:notification/server/saved` | yes | no | no |
| `minecraft:notification/allowlist/*` | management API changes only | no | no |
| `minecraft:notification/bans/*` | management API changes only | no | no |
| `minecraft:notification/ip_bans/*` | management API changes only | no | no |
| `minecraft:notification/operators/*` | management API changes only | no | no |
| `minecraft:notification/gamerules/*` | management API changes only | no | no |

## Configuration

The service is disabled by default. Enable it only after setting a secret and deciding where it should listen.

Spigot/Paper uses `plugins/MinecraftServerManagementProtocolLegacySupport/config.yml`:

```yaml
management-server-enabled: true
management-server-host: localhost
management-server-port: 25585
management-server-secret: 'replaceWithExactly40AlphaNumericCharacters'
management-server-allowed-origins: ''
management-server-tls-enabled: false
management-server-tls-keystore: ''
management-server-tls-keystore-password: ''
```

Velocity and BungeeCord create `management-server.properties` in the plugin data folder with the same setting names.

### Settings

- `management-server-enabled`: Starts or disables the management endpoint.
- `management-server-host`: Interface to bind. Use `localhost` when only local tooling or a reverse proxy should connect.
- `management-server-port`: Port for the WebSocket server. Use a fixed port for production.
- `management-server-secret`: Required shared secret. It must be exactly 40 alphanumeric characters.
- `management-server-allowed-origins`: Comma-separated list of allowed browser origins when authenticating through `Sec-WebSocket-Protocol`.
- `management-server-tls-enabled`: Enables `wss://`. Requires a valid keystore.
- `management-server-tls-keystore`: Path to a PKCS12 keystore.
- `management-server-tls-keystore-password`: Keystore password. It can also be supplied through `MINECRAFT_MANAGEMENT_TLS_KEYSTORE_PASSWORD` or the JVM property `management.tls.keystore.password`.

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE).
