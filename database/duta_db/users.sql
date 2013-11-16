CREATE TABLE [dbo].[users]
(
	[user_id] INT NOT NULL  IDENTITY, 
    [login] NVARCHAR(50) NOT NULL, 
    [password] NVARCHAR(50) NOT NULL, 
    [status] INT NOT NULL, 
    [description] NVARCHAR(128) NOT NULL, 
    [last_status_update] DATETIME NOT NULL, 
    PRIMARY KEY ([user_id]), 
    CONSTRAINT [PK_users_login] UNIQUE ([login]) 
)
