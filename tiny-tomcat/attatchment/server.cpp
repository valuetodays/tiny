#include <stdio.h>
#include <winsock2.h>

#pragma comment(lib, "ws2_32.lib")

void handleRequest(SOCKET requestFromClient);


int main()
{

   
    WSADATA wsaData;
    int iResult = WSAStartup( MAKEWORD(2,2), &wsaData );
    if ( iResult != NO_ERROR )
        printf("Error at WSAStartup()\n");

    
	SOCKET server;
    server = socket( AF_INET, SOCK_STREAM, IPPROTO_TCP );

    if ( server == INVALID_SOCKET ) {
        printf( "Error at socket(): %ld\n", WSAGetLastError() );
        WSACleanup();
        return 1;
    }

    
    sockaddr_in service;

    service.sin_family = AF_INET;
    service.sin_addr.s_addr = inet_addr( "127.0.0.1" );
    service.sin_port = htons( 27015 );

    if ( bind( server, (SOCKADDR*) &service, sizeof(service) ) == SOCKET_ERROR ) {
        printf( "bind() failed.\n" );
        closesocket(server);
        return 1;
    }

    
    if ( listen( server, 1 ) == SOCKET_ERROR )
        printf( "Error listening on socket.\n");



    printf( "Waiting for a client to connect...\n" );
    while (1) {
       
        SOCKET acceptSocket = SOCKET_ERROR;
        while ( acceptSocket == SOCKET_ERROR ) {
            acceptSocket = accept( server, NULL, NULL );
        }
        printf( "Client Connected.\n");
        handleRequest(acceptSocket);

    }

                                                                                   

	getchar();
	WSACleanup();
    return 0;
}

void handleRequest(SOCKET server)
{
    int bytesSent;
    int bytesRecv = SOCKET_ERROR;
	char sendbuf[1024] = "<xml><head><version>1.0</version><service>jee.billy.orderquery</service><mid>1000001</mid></head><body><oid>12356</oid><flag>0000</flag></body></xml>";
    char recvbuf[1024] = "";

    bytesRecv = recv( server, recvbuf, 1024, 0 );
    printf( "[%s] Bytes Recv: %ld\n", recvbuf, bytesRecv );

    bytesSent = send( server, sendbuf, strlen(sendbuf), 0 );
    printf( "[%s] Bytes Sent: %ld\n", sendbuf, bytesSent );
	closesocket(server);

}
