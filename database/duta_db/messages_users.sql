CREATE TABLE [dbo].[messages_users]
(
    [user_id] INT NOT NULL, 
    [message_id] INT NOT NULL, 
    PRIMARY KEY ([message_id], [user_id]), 
    CONSTRAINT [FK_messages_users_user] FOREIGN KEY ([user_id]) REFERENCES [users]([user_id]), 
    CONSTRAINT [FK_messages_users_message] FOREIGN KEY ([message_id]) REFERENCES [messages]([message_id])
)
