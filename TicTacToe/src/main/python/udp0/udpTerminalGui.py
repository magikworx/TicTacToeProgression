import socket
import sys
import threading

from functools import partial
from PyQt5.Qt import *

import game


class Worker(QRunnable):
    board = pyqtSignal(object)
    move_request = pyqtSignal()

    def __init__(self, *args, **kwargs):
        super(Worker, self).__init__()

    @pyqtSlot()
    def run(self):
        serverAddressPort = ("127.0.0.1", 8889)
        bufferSize = 1024
        with socket.socket(family=socket.AF_INET, type=socket.SOCK_DGRAM) as sock:
            last = -1
            while True:
                sock.sendto(bytes([0, 0, 0]), serverAddressPort)
                (response_bytes, response_sock) = sock.recvfrom(bufferSize)
                player = int(response_bytes[1])
                board = response_bytes[2:]

                newHash = game.Rules.hash_board(board)
                if last != newHash:
                    # print_board(board)
                    last = newHash


class PushButton(QPushButton):
    def __init__(self, text, parent=None):
        super(PushButton, self).__init__(text, parent)

        self.setText(text)
        self.setMinimumSize(QSize(50, 50))
        self.setMaximumSize(QSize(50, 50))


class MyWindow(QMainWindow):
    def __init__(self):
        super(MyWindow, self).__init__()

        centralWidget = QWidget()
        self.setCentralWidget(centralWidget)

        self.label = QLabel(self, alignment=Qt.AlignRight)
        self.label.setFont(QFont("Times", 12, QFont.Bold))

        self.layout = QGridLayout(centralWidget)
        self.layout.addWidget(self.label, 0, 0, 1, 3)

        i = 0
        for row in range(3):
            for column in range(3):
                button = PushButton(f'', self)
                button.clicked.connect(partial(self.onClicked, row, column))
                self.layout.addWidget(button, row + 1, column)
        # self.background_thread = threading.Thread(target=background).start()

    def onClicked(self, row, col):
        pass


if __name__ == '__main__':
    app = QApplication(sys.argv)
    w = MyWindow()
    w.setWindowTitle('TicTacToe')
    w.show()
    sys.exit(app.exec_())