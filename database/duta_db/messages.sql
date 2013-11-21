CREATE TABLE [dbo].[messages]
(
	[message_id] INT NOT NULL PRIMARY KEY IDENTITY, 
    [time] DATETIME NOT NULL, 
    [author_id] INT NOT NULL, 
    [message] NVARCHAR(MAX) NOT NULL, 
    CONSTRAINT [FK_messages_author] FOREIGN KEY ([author_id]) REFERENCES [users]([user_id])
)
