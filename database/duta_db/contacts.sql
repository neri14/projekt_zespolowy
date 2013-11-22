CREATE TABLE [dbo].[contacts]
(
	[id] INT NOT NULL PRIMARY KEY IDENTITY, 
    [name] NVARCHAR(50) NOT NULL, 
    [user_id] INT NOT NULL, 
    [contact_id] INT NOT NULL, 
    CONSTRAINT [FK_contacts_user] FOREIGN KEY ([user_id]) REFERENCES [users]([user_id]), 
    CONSTRAINT [FK_contacts_contact] FOREIGN KEY ([user_id]) REFERENCES [users]([user_id]) 
)
