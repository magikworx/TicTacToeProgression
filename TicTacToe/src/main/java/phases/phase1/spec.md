# Packet Structure

### three-byte request packet [0,1,2]
    [command, row, col]

### byte-encoded string response
Will contain the value of the board to be printed to the screen

#### Get Board
Request: [ 0x00, - , - ]

#### New Game 
Request: [ 0x01, - , - ]

#### Make Move
Request: [ 0x02, row, col ]

