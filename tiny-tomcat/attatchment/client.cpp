#include <stdio.h>
#include <winsock2.h>
#pragma comment(lib, "ws2_32.lib")

int main()
{  
	// 初始化 Winsock.
    WSADATA wsaData;
    int iResult = WSAStartup( MAKEWORD(2,2), &wsaData );
    if ( iResult != NO_ERROR )
        printf("Error at WSAStartup()\n");

    // 建立socket socket.
    SOCKET client;
    client = socket( AF_INET, SOCK_STREAM, IPPROTO_TCP );

    if ( client == INVALID_SOCKET ) 
	{
        printf( "Error at socket(): %ld\n", WSAGetLastError() );
        WSACleanup();
        return 0;
    }

     // 连接到服务器.
    sockaddr_in clientService;

    clientService.sin_family = AF_INET;
    clientService.sin_addr.s_addr = inet_addr( "127.0.0.1" );
    clientService.sin_port = htons( 27015 );

    if ( connect( client, (SOCKADDR*) &clientService, sizeof(clientService) ) == SOCKET_ERROR) {
        printf( "Failed to connect.\n" );
        WSACleanup();
        return 0;
    }

    // 发送并接收数据.
    int bytesSent;
    int bytesRecv = SOCKET_ERROR;
    char sendbuf[1024] = "<xml><head><version>1.0</version><service>jee.billy.orderquery</service><mid>1000001</mid></head><body><oid>12356</oid></body></xml>\r\n";
    char recvbuf[1024] = "";

	// 发送数据
    bytesSent = send( client, sendbuf, strlen(sendbuf), 0 );
    printf( "Bytes Sent: %ld\n", bytesSent );

    // 接收数据
    bytesRecv = recv( client, recvbuf, 1024, 0 );
	printf( "Bytes Recv: %ld\n", bytesRecv );

    if ( bytesRecv == 0 || bytesRecv == WSAECONNRESET ) 
	{
        printf( "Connection Closed.\n");
		WSACleanup();
        return -1;
    }
    if (bytesRecv < 0) 
	{
        WSACleanup();
		return -1;
	}
	printf("response from server: %s\r\n", recvbuf);
	
   
	closesocket(client);
	WSACleanup();
	printf("end....\r\n");
	
	getchar();
    return 0;
}



