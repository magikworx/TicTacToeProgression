//
// Quick UDP (quick_udp.h)
//
// A simple UDP networking library.
// - Works on UNIX (Linux, macOS, BSD, ...) and Windows.
// - Supports IPv4 and IPv6.
// - No malloc - you supply storage for everything.
//
// Include the file like this in ONE source file
// then use as a normal header file in any others:
//
//      #define QUICK_UDP_IMPLEMENTATION
//      #include "quick_udp.h"
//
// by Benedict Henshaw
// https://github.com/benhenshaw/quick_udp
//
// PUBLIC DOMAIN - See license at the end of this file.
//

#ifndef QUICK_UDP_INCLUDED
#define QUICK_UDP_INCLUDED

#ifdef __cplusplus
extern "C" {
#endif

#ifdef _WIN32
#include <winsock2.h>
#include <ws2tcpip.h>
typedef int socklen_t;
#else
#include <sys/types.h>
    #include <sys/socket.h>
    #include <sys/ioctl.h>
    #include <netinet/in.h>
    #include <arpa/inet.h>
    #include <netdb.h>
    #include <fcntl.h>
    #include <unistd.h>
    #define INVALID_SOCKET -1
    #define SOCKET_ERROR -1
    #define NO_ERROR 0
    #define SOCKET int
#endif

// A qu_handle is used when sending and receiving data.
typedef struct qu_handle
{
    SOCKET socket;
    struct sockaddr_storage address;
    int length;
} qu_handle;

// Call this before using the library.
// Returns 1 on success, 0 on failure.
int qu_init(void);

// Call this when you're done.
// Returns 1 on success, 0 on failure.
int qu_shutdown(void);

// Start listening to incoming packets.
// Returns 1 on success, 0 on failure.
// handle
//      On success will contain a valid qu_handle, ready for use in
//      a call to qu_receive. On failure will remain unmodified.
// port
//      The network port that you want to bind to.
int qu_listen(qu_handle * handle, int port);

// Set up a qu_handle for sending and receiving data from a specific address.
// Returns 1 on success, 0 on failure.
// handle
//      On success will contain a valid qu_handle, ready for use in
//      a call to qu_send. On failure will remain unmodified.
//      Be aware that you cannot receive packets from any sender using
//      this handle - only the machine at the address you specified.
//      If you need this functionality see qu_listen() above.
// address
//      A ASCII string containing the address of the machine you
//      want to connect to.
// port
//      The port of the machine you want to connect to.
int qu_connect(qu_handle * handle, char * address, int port);

// Close the handle when you are finished with it.
// Only affects handles obtained from qu_connect and qu_listen.
// You don't need to call this on sender handles from qu_receive.
void qu_close(qu_handle * handle);

// Receive a packet sent to this handle.
// Returns the number of bytes received. If this handle is non-blocking
// and there are no packets waiting to be received then it will return 0.
// handle
//      A handle obtained from qu_listen or qu_connect.
// sender
//      A handle to the sender of this data. Can be used to send data back.
//      OPTIONAL - you can pass NULL (0) if you don't care who sent it.
// buffer
//      A valid buffer, to be filled with data from the received packet.
// buffer_length
//      The size in bytes of 'buffer'. If the packet contains more data
//      than 'buffer_length', the data will be truncated.
//      In real-world networking, most packets cannot be larger than around
//      1400 bytes. Look up "Maximum Transmission Unit" for more info.
int qu_receive(qu_handle * handle, qu_handle * sender,
               void * buffer, size_t buffer_length);

// Send data to an existing handle. This could be a handle from a call to
// qu_connect, or a sender handle from qu_receive.
// Returns the number of bytes actually sent.
// handle
//      The handle to send data to.
// data
//      Data to send.
// byte_count
//      The size in bytes of the data pointed to by 'data'.
int qu_send(qu_handle * handle, void * data, int byte_count);

// Calls to qu_receive are blocking by default, but you can make them
// non-blocking by using qu_disable_blocking.
// Returns 1 if change was made successfully, 0 on error.
// handle
//      The handle to modify.
// disable_blocking
//      if 1 calls using 'handle' will NOT block.
//      if 0 calls using 'handle' WILL block.
int qu_disable_blocking(qu_handle * handle, int disable_blocking);

// Call this to block until there is a packet ready to be received on
// the given handle.
// Returns:
//      1 if there is a packet.
//      0 if not (a time-out occurred).
//     -1 on error.
// handle
//      The handle to wait for packets from.
// timeout_ms
//      The number of milliseconds to wait for a packet.
//      Pass 0 to wait indefinitely.
int qu_wait(qu_handle * handle, int timeout_ms);

// Compare two handles to see if they are the same. Most common  use
// is to check the sender of a received packet.
// BEWARE: This is not a secure method of checking if a packet came
// from who you expected it to, as UDP packets can be modified to
// have any sender address in them. Also, if a remote user's network
// state changes, their handle may change.
// If you need to strongly verify where a packet is from you will
// need to employ additional methods of verification.
// Returns 1 if handles appear the same, 0 if not.
int qu_compare(qu_handle * a, qu_handle * b);

#ifdef __cplusplus
}
#endif

#endif

#ifdef QUICK_UDP_IMPLEMENTATION

int qu_init(void)
{
#ifdef _WIN32
    WSADATA wsa_data;
    if (WSAStartup(MAKEWORD(2, 2), &wsa_data) != 0) return 0;
#endif
    return 1;
}

int qu_shutdown(void)
{
#ifdef _WIN32
    return WSACleanup() != SOCKET_ERROR;
#endif
    return 1;
}

void qu__close_socket_number(SOCKET socket_number)
{
    if (socket_number != INVALID_SOCKET)
    {
#ifdef _WIN32
        closesocket(socket_number);
#else
        close(socket_number);
#endif
    }
}

void qu_close(qu_handle * handle)
{
    qu__close_socket_number(handle->socket);
}

int qu_listen(qu_handle * handle, int port)
{
    if (!handle) return 0;

    struct addrinfo hints = { 0 };
    hints.ai_family   = AF_UNSPEC;
    hints.ai_socktype = SOCK_DGRAM;
    hints.ai_flags    = AI_PASSIVE;

    char port_string[16];
    snprintf(port_string, 16, "%d", port);

    struct addrinfo * info_list = NULL;
    int result = getaddrinfo(NULL, port_string, &hints, &info_list);
    if (result != 0) return 0;

    SOCKET socket_number = INVALID_SOCKET;
    struct addrinfo * i = NULL;
    for (i = info_list; i != NULL; i = i->ai_next)
    {
        socket_number = socket(i->ai_family, i->ai_socktype, i->ai_protocol);
        if (socket_number == INVALID_SOCKET)
        {
            continue;
        }

        int result = bind(socket_number, i->ai_addr, i->ai_addrlen);
        if (result == SOCKET_ERROR) {
            qu__close_socket_number(socket_number);
            continue;
        }

        break;
    }

    freeaddrinfo(info_list);

    if (i == NULL) return 0;

    memset(handle, 0, sizeof(*handle));
    handle->socket = socket_number;
    memcpy(&handle->address, i->ai_addr, i->ai_addrlen);
    handle->length = i->ai_addrlen;

    return 1;
}

int qu_connect(qu_handle * handle, const char * address, int port)
{
    if (!handle) return 0;

    struct addrinfo hints = { 0 };
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_DGRAM;

    char port_string[16];
    snprintf(port_string, 16, "%d", port);

    struct addrinfo * info_list = NULL;
    int result = getaddrinfo(address, port_string, &hints, &info_list);
    if (result != 0)
    {
        return 0;
    }

    SOCKET socket_number = 0;
    struct addrinfo * i = NULL;
    for (i = info_list; i != NULL; i = i->ai_next)
    {
        socket_number = socket(i->ai_family, i->ai_socktype, i->ai_protocol);
        if (socket_number == INVALID_SOCKET) continue;
        break;
    }

    if (i == NULL || socket_number == INVALID_SOCKET)
    {
        freeaddrinfo(info_list);
        return 0;
    }

    memset(handle, 0, sizeof(*handle));
    handle->socket = socket_number;
    memcpy(&handle->address, i->ai_addr, i->ai_addrlen);
    handle->length = i->ai_addrlen;

    freeaddrinfo(info_list);
    return 1;
}

int qu_send(qu_handle * handle, void * data, int byte_count)
{
    if (!handle || handle->socket == INVALID_SOCKET) return -1;
    int bytes_sent = sendto(
        handle->socket,
        (const char*)data,
        byte_count,
        0,
        (struct sockaddr *)&handle->address,
        handle->length
    );
    return bytes_sent;
}

int qu_receive(qu_handle * handle, qu_handle * sender, void * buffer, size_t buffer_length)
{
    if (handle->socket == INVALID_SOCKET || buffer == NULL) return -1;

    struct sockaddr_storage sender_address;
    socklen_t address_length = sizeof(sender_address);

    int bytes_received = recvfrom(handle->socket, (char*)buffer, buffer_length, 0,
        (struct sockaddr *)&sender_address, &address_length);

    if (sender)
    {
        memset(sender, 0, sizeof(*sender));
        sender->socket  = handle->socket;
        sender->address = sender_address;
        sender->length  = address_length;
    }

    return bytes_received;
}

int qu_disable_blocking(qu_handle * handle, int disable_blocking)
{
    if (handle->socket == INVALID_SOCKET) return 0;

#ifdef _WIN32
    unsigned long n = (disable_blocking != 0);
    int error = ioctlsocket(handle->socket, FIONBIO, &n);
    return !error;
#else
    int flags = fcntl(handle->socket, F_GETFL);
    if (flags == -1) return -1;
    if (disable_blocking) flags |= O_NONBLOCK; else flags &= ~O_NONBLOCK;
    int result = fcntl(handle->socket, F_SETFL, flags);
    return !!(result & O_NONBLOCK);
#endif
}

int qu_wait(qu_handle * handle, int timeout_ms)
{
    if (handle->socket == INVALID_SOCKET) return -1;

    struct timeval t = {
        .tv_sec  =  timeout_ms / 1000,
        .tv_usec = (timeout_ms % 1000) * 1000,
    };

    fd_set set;
    FD_ZERO(&set);
    FD_SET(handle->socket, &set);

    return select(handle->socket + 1, &set, NULL, NULL, timeout_ms > 0 ? &t : NULL);
}

int qu_compare(qu_handle * a, qu_handle * b)
{
    if (!a || !b) return 0;
    if (a == b) return 1;
    if (a->length != b->length) return 0;
    return memcmp(&a->address, &b->address, a->length) == 0;
}

/*
    This is free and unencumbered software released into the public domain.

    Anyone is free to copy, modify, publish, use, compile, sell, or
    distribute this software, either in source code form or as a compiled
    binary, for any purpose, commercial or non-commercial, and by any
    means.

    In jurisdictions that recognize copyright laws, the author or authors
    of this software dedicate any and all copyright interest in the
    software to the public domain. We make this dedication for the benefit
    of the public at large and to the detriment of our heirs and
    successors. We intend this dedication to be an overt act of
    relinquishment in perpetuity of all present and future rights to this
    software under copyright law.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
    EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
    IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
    OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
    ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
    OTHER DEALINGS IN THE SOFTWARE.
*/

#endif
