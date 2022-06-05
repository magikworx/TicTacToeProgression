#define OLC_PGE_APPLICATION
#include "olcPixelGameEngine.h"

#include "znet.h"

#include <thread>

#define MYDATA_BUFLEN 1024

typedef struct MyData {
    char buffer[MYDATA_BUFLEN];
    int idx = 1;
    int count = 0;
} MyData;

class game {
    int connect_status = -1;
    bool should_exit = false;
    int player = 0;
    char board [9]{};

    MyData buffer;
    zn_State  *event_loop_state = nullptr; /* the znet event loop handler */
    zn_Tcp    *handle = nullptr;

    std::thread background_worker{};
public:
    game() {
        zn_initialize();
    }
    void start() {
        event_loop_state = zn_newstate();
        if (event_loop_state == nullptr) {
            fprintf(stderr, "create znet handler failured\n");
            connect_status = 1;
            return ; /* error out */
        }
        handle = zn_newtcp(event_loop_state);
        zn_connect(handle, "localhost", 8889, 0, on_connection, &buffer);
        background_worker = std::thread{&game::work, this};
        connect_status = 0;
    }
    void stop() {
        should_exit = true;
        background_worker.join();
        zn_deinitialize();
    }

    bool my_turn() {
        int empty = 0;
        for(int i = 0; i < 9; ++i) {
            if (board[i] == 0) {
                ++empty;
            }
        }
        bool x_turn = empty % 2 == 1;
        return (x_turn && player == 1) || (!x_turn && player == 2);
    }

    char* get_board() {
        return board;
    }

    void make_move(int row, int col) {
        if(my_turn()) {
            const char move[] {1,(char)row,(char) col };
            qu_send(&handle, (void*)move, 3);
        }
    }

    int row_win() {
        for(int row = 0; row < 3; ++ row) {
            int cell0 = 3 * row + 0;
            int cell1 = 3 * row + 1;
            int cell2 = 3 * row + 2;
            if (board[cell0] == board[cell1] && board[cell0] == board[cell2]) {
                return board[cell0];
            }
        }
        return 0;
    }
    int col_win() {
        for(int col = 0; col < 3; ++ col) {
            int cell0 = 3 * 0 + col;
            int cell1 = 3 * 1 + col;
            int cell2 = 3 * 2 + col;
            if (board[cell0] == board[cell1] && board[cell0] == board[cell2]) {
                return board[cell0];
            }
        }
        return 0;
    }
    int diag_win() {
        int cell0 = 3 * 0 + 0;
        int cell1 = 3 * 1 + 1;
        int cell2 = 3 * 2 + 2;
        if (board[cell0] == board[cell1] && board[cell0] == board[cell2]) {
            return board[cell0];
        }
        cell0 = 3 * 0 + 2;
        cell1 = 3 * 1 + 1;
        cell2 = 3 * 2 + 0;
        if (board[cell0] == board[cell1] && board[cell0] == board[cell2]) {
            return board[cell0];
        }
        return 0;
    }
    int get_winner() {
        int row_winner = row_win();
        if (row_winner > 0) {
            return row_winner;
        }
        int col_winner = col_win();
        if (col_winner > 0) {
            return col_winner;
        }
        int diag_winner = diag_win();
        if (diag_winner > 0) {
            return diag_winner;
        }
        return 0;
    }
    bool is_win() {
        int winner = get_winner();
        if (winner == player) {
            return true;
        }
        return false;
    }
    bool is_lose() {
        int winner = get_winner();
        if (winner == player) {
            return true;
        }
        return false;
    }
    bool is_draw() {
        int empty = 0;
        for(int i = 0; i < 9; ++i) {
            if (board[i] == 0) {
                ++empty;
            }
        }
        return empty == 0 && get_winner() == 0;
    }
    bool is_gameover() {
        if (get_winner() > 0 || is_draw()) {
            return true;
        }
        return false;
    }
private:
    void work() {
        char buffer[1024];
        memset(buffer, 0, 1024);

        while(!should_exit) {
            const char get_status[] {0,0,0};
            qu_send(&handle, (void*)get_status, 3);
            memset(buffer, 0, 1024);
            qu_handle sender;
            int recv_size = qu_receive(&handle, &sender, buffer, 1024 - 1);
            if (recv_size == 11 && buffer[0] == 0) {
                player = buffer[1];
                memcpy(board, buffer+2, 9);
            } else if(recv_size == 2 && buffer[0] == 1) {
                std::cout << "made move" << std::endl;
            }
            std::this_thread::sleep_for(std::chrono::milliseconds(100));
        }
    }
};

int char_width = 7;
int char_margin = 1;
int line_width = 1;
int box_width = char_width + 2 * char_margin;
int total_boxes_width = box_width * 3;
int total_line_width = line_width * 4;
int game_size = total_boxes_width + total_line_width;

// Override base class with your custom functionality
class TicTacToeGui : public olc::PixelGameEngine
{
    game* _g;
public:
    TicTacToeGui(game* g)
	{
		// Name your application
		sAppName = "TicTacToe";
        _g = g;
	}

public:
	bool OnUserCreate() override
	{
		return true;
	}

    void DrawBox(int row, int col) {
        int side_length = box_width + line_width;
        int x_1 = col * side_length;
        int x_2 = (col+1) * side_length;
        int y_1 = row * side_length;
        int y_2 = (row+1) * side_length;
        // top
        DrawLine(x_1, y_1, x_2, y_1);
        // right
        DrawLine(x_2, y_1, x_2, y_2);
        // bottom
        DrawLine(x_1, y_2, x_2, y_2);
        //left
        DrawLine(x_1, y_1, x_1, y_2);
    }

    void DrawX(int row, int col) {
        int x_1 = col * (box_width + line_width) + char_margin + line_width;
        int x_2 = x_1 + (char_width-1);
        int y_1 = row * (box_width + line_width) + char_margin + line_width;
        int y_2 = y_1 + (char_width-1);
        DrawLine(x_1, y_1, x_2, y_2);
        DrawLine(x_2, y_1, x_1, y_2);
    }

    void DrawO(int row, int col) {
        int x = col * (box_width + line_width) + box_width/2 + char_margin;
        int y = row * (box_width + line_width) + box_width/2 + char_margin;
        int radius = char_width / 2;
        DrawCircle(x, y, radius);
    }

    void DrawGrid() {
        for(int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                DrawBox(row, col);
            }
        }
    }

    void DrawGameOver() {
        int max_size = GetDrawTargetWidth()-line_width;
        int rect_x = 0;
        int rect_y = GetDrawTargetHeight()/4;
        FillRect(rect_x, rect_y, max_size, rect_y*2, olc::BLACK);
        DrawRect(rect_x, rect_y, max_size, rect_y*2);
        std::string status = "Lose";
        if (_g->is_draw()) {
            status = "Draw";
        } else if (_g->is_win()) {
            status = "Win";
        }
        auto size = GetTextSizeProp(status);
        int string_x = (GetDrawTargetWidth() - size.x)/2 + line_width;
        int string_y = (GetDrawTargetHeight() - size.y)/2 + line_width;
        DrawStringProp(string_x, string_y, status);
    }

    void handle_click(int x, int y) {
        int row_offset = line_width;
        for(int row = 0; row < 3; ++row) {
            if (y >= row_offset && y < row_offset + box_width ) {
                int col_offset = line_width;
                for (int col = 0; col < 3; ++col) {
                    if (x >= col_offset && x < col_offset + box_width) {
                        _g->make_move(row, col);
                        return;
                    }
                    col_offset += line_width + box_width;
                }
            }
            row_offset += line_width + box_width;
        }
    }

    float elapsed = 0;
	bool OnUserUpdate(float fElapsedTime) override
	{
        if (GetMouse(0).bReleased) {
            int x = GetMouseX();
            int y = GetMouseY();
            handle_click(x, y);
        }
        if (elapsed > 1) {
            char *board = _g->get_board();

            Clear(olc::BLACK);
            DrawGrid();
            for (int row = 0; row < 3; ++row) {
                for (int col = 0; col < 3; ++col) {
                    int idx = row * 3 + col;
                    if (board[idx] == 1) {
                        DrawX(row, col);
                    } else if (board[idx] == 2) {
                        DrawO(row, col);
                    }
                }
            }
            if (_g->is_gameover()) {
                DrawGameOver();
            }
            elapsed = 0;
        }
        elapsed += fElapsedTime;
        std::this_thread::sleep_for(std::chrono::milliseconds(10));
		return true;
	}
};

int main()
{
    game g;
    g.start();
    TicTacToeGui demo(&g);
    if (demo.Construct(game_size, game_size, 10, 10))
        demo.Start();
    g.stop();
	return 0;
}