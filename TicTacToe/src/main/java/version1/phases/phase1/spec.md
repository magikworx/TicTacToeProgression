# Packet Structure

### three-byte request packet [0,1,2]
    [command, row, col] => row, col optional

### byte-encoded string response
Will contain the value of the board to be printed to the screen

#### Get Status
Request: [ 0x00, - , - ]

Response: [ 0x00, status, board]
- status(1-byte):
  - 0x00 waiting for other player
  - 0x01 your move
  - 0x02 you win
  - 0x03 you lose
  - 0x04 draw
- board(9-bytes) 
  - {0,1,2,3,4,5,6,7,8} =>
    - |0|1|2|
    - |3|4|5|
    - |6|7|8|

#### Make Move
Request: [ 0x01, row, col ]

Response: [ 0x01, code] 
- code(1-byte):
    - 0x00 accepted
    - 0x01 invalid row
    - 0x02 invalid column
    - 0x03 already taken
