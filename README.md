<small>Just another README.md</small>

# Quiz Project for Raspberry Pi

## Features:

- The beginning for the server (everything network and communication related)
- DevClient for testing
- Headless Client

## How to set up: Raspberry Pi Server

- Server as AP
- NTP Server (with Broadcast)
- Server IP: 172.24.1.1/24
- Server needs Desktop environment for Application
- Just start the Java application

## How to set up: Raspberry Pi Client

- Clients via DHCP
- Clients starting -> Autoconnect to above IP (i.e. using .bashrc and auto login)
- Just start the application (CLI only)
- Just press enter after connecting to server, the rest will be handled by server
