USE [master]
GO

/* For security reasons the login is created disabled and with a random password. */
/****** Object:  Login [desafio]    Script Date: 26/08/2025 22:40:36 ******/
CREATE LOGIN [desafio] WITH PASSWORD=N'desafio', DEFAULT_DATABASE=[DESAFIO_API], DEFAULT_LANGUAGE=[Português (Brasil)], CHECK_EXPIRATION=ON, CHECK_POLICY=ON
GO

ALTER SERVER ROLE [sysadmin] ADD MEMBER [desafio]
GO

ALTER SERVER ROLE [serveradmin] ADD MEMBER [desafio]
GO


