# Tic Tac Toe Progression
## current folders
### base
base game implementation
### starter
base local players and AIs
### udp0
simple udp implementation
### udp1
- packet loss tolerant udp connection with retry
- introduced a fault injection class
  - The fault injector is a server/client that passes messages back and forth between the existing client and gui, a man-in-the-middle.
  - The fault injector for the next stage introduces the fault that makes the previous stage unsuitable.
  - For instance, in packet loss we randomly don't forward packets.
### udp2
- packet loss tolerant with retry
- corruption tolerant with TCP checksums
- expanded fault injection capability
  - we randomly scramble the bytes
### udp3
- message ids for matching retries
- message id history for rejecting retries
- expanded fault injection capability
  - we late send packets by swapping a packet with its retry
### util
utilities and base structures for packing and transporting data
## future folders
### tcp0
- tcp buys reliability and smarter control logic
- multi-threaded connections for many clients
- more than one game instance
- leader boards
### objectstreams0
- buys ability to transmit classes across a connection and intelligently use remote objects
- major cleanup from tcp object transmission
## future tech
### second game
- connect 4?
### load balancers
- spread the request load to multiple server instances
- same service, many servers
### proxies / gateways
- forward traffic through different routes
- provide services on many servers through unified gateway
- many servers, single server
### directory server
- intelligent routing to different remote gateways
### cdn(?)
- redirect traffic for static objects to more local servers

