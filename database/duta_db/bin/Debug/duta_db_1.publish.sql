﻿/*
Deployment script for duta_20131116_1822

This code was generated by a tool.
Changes to this file may cause incorrect behavior and will be lost if
the code is regenerated.
*/

GO
SET ANSI_NULLS, ANSI_PADDING, ANSI_WARNINGS, ARITHABORT, CONCAT_NULL_YIELDS_NULL, QUOTED_IDENTIFIER ON;

SET NUMERIC_ROUNDABORT OFF;


GO
:setvar DatabaseName "duta_20131116_1822"
:setvar DefaultFilePrefix "duta_20131116_1822"
:setvar DefaultDataPath "C:\Program Files\Microsoft SQL Server\MSSQL11.MSSQLSERVER\MSSQL\DATA\"
:setvar DefaultLogPath "C:\Program Files\Microsoft SQL Server\MSSQL11.MSSQLSERVER\MSSQL\DATA\"

GO
:on error exit
GO
/*
Detect SQLCMD mode and disable script execution if SQLCMD mode is not supported.
To re-enable the script after enabling SQLCMD mode, execute the following:
SET NOEXEC OFF; 
*/
:setvar __IsSqlCmdEnabled "True"
GO
IF N'$(__IsSqlCmdEnabled)' NOT LIKE N'True'
    BEGIN
        PRINT N'SQLCMD mode must be enabled to successfully execute this script.';
        SET NOEXEC ON;
    END


GO
IF EXISTS (SELECT 1
           FROM   [master].[dbo].[sysdatabases]
           WHERE  [name] = N'$(DatabaseName)')
    BEGIN
        ALTER DATABASE [$(DatabaseName)]
            SET ANSI_NULLS ON,
                ANSI_PADDING ON,
                ANSI_WARNINGS ON,
                ARITHABORT ON,
                CONCAT_NULL_YIELDS_NULL ON,
                QUOTED_IDENTIFIER ON,
                ANSI_NULL_DEFAULT ON,
                CURSOR_DEFAULT LOCAL 
            WITH ROLLBACK IMMEDIATE;
    END


GO
IF EXISTS (SELECT 1
           FROM   [master].[dbo].[sysdatabases]
           WHERE  [name] = N'$(DatabaseName)')
    BEGIN
        ALTER DATABASE [$(DatabaseName)]
            SET PAGE_VERIFY NONE 
            WITH ROLLBACK IMMEDIATE;
    END


GO
USE [$(DatabaseName)];


GO
PRINT N'Rename refactoring operation with key 6c803c3e-6560-4862-8c0c-07e837507478 is skipped, element [dbo].[users].[Id] (SqlSimpleColumn) will not be renamed to user_id';


GO
PRINT N'Rename refactoring operation with key e6dd91b3-53bf-4a90-b005-4f11616b9ce3 is skipped, element [dbo].[contacts].[Id] (SqlSimpleColumn) will not be renamed to id';


GO
PRINT N'Rename refactoring operation with key d749da46-c668-482c-9c1a-d739df8c8438 is skipped, element [dbo].[messages].[Id] (SqlSimpleColumn) will not be renamed to message_id';


GO
PRINT N'Rename refactoring operation with key 0fd29cdd-50e4-4e0b-ae4a-2076ec2e92d7 is skipped, element [dbo].[messages_users].[Id] (SqlSimpleColumn) will not be renamed to id';


GO
PRINT N'Creating [dbo].[contacts]...';


GO
CREATE TABLE [dbo].[contacts] (
    [id]         INT           IDENTITY (1, 1) NOT NULL,
    [name]       NVARCHAR (50) NOT NULL,
    [user_id]    INT           NOT NULL,
    [contact_id] INT           NOT NULL,
    PRIMARY KEY CLUSTERED ([id] ASC)
);


GO
PRINT N'Creating [dbo].[messages]...';


GO
CREATE TABLE [dbo].[messages] (
    [message_id] INT            IDENTITY (1, 1) NOT NULL,
    [time]       DATETIME       NOT NULL,
    [author_id]  INT            NOT NULL,
    [message]    NVARCHAR (MAX) NOT NULL,
    PRIMARY KEY CLUSTERED ([message_id] ASC)
);


GO
PRINT N'Creating [dbo].[messages_users]...';


GO
CREATE TABLE [dbo].[messages_users] (
    [user_id]    INT NOT NULL,
    [message_id] INT NOT NULL,
    PRIMARY KEY CLUSTERED ([message_id] ASC, [user_id] ASC)
);


GO
PRINT N'Creating [dbo].[users]...';


GO
CREATE TABLE [dbo].[users] (
    [user_id]            INT            IDENTITY (1, 1) NOT NULL,
    [login]              NVARCHAR (50)  NOT NULL,
    [password]           NVARCHAR (50)  NOT NULL,
    [status]             INT            NOT NULL,
    [description]        NVARCHAR (128) NOT NULL,
    [last_status_update] DATETIME       NOT NULL,
    PRIMARY KEY CLUSTERED ([user_id] ASC),
    CONSTRAINT [PK_users_login] UNIQUE NONCLUSTERED ([login] ASC)
);


GO
PRINT N'Creating FK_contacts_user...';


GO
ALTER TABLE [dbo].[contacts] WITH NOCHECK
    ADD CONSTRAINT [FK_contacts_user] FOREIGN KEY ([user_id]) REFERENCES [dbo].[users] ([user_id]);


GO
PRINT N'Creating FK_contacts_contact...';


GO
ALTER TABLE [dbo].[contacts] WITH NOCHECK
    ADD CONSTRAINT [FK_contacts_contact] FOREIGN KEY ([user_id]) REFERENCES [dbo].[users] ([user_id]);


GO
PRINT N'Creating FK_messages_author...';


GO
ALTER TABLE [dbo].[messages] WITH NOCHECK
    ADD CONSTRAINT [FK_messages_author] FOREIGN KEY ([author_id]) REFERENCES [dbo].[users] ([user_id]);


GO
PRINT N'Creating FK_messages_users_user...';


GO
ALTER TABLE [dbo].[messages_users] WITH NOCHECK
    ADD CONSTRAINT [FK_messages_users_user] FOREIGN KEY ([user_id]) REFERENCES [dbo].[users] ([user_id]);


GO
PRINT N'Creating FK_messages_users_message...';


GO
ALTER TABLE [dbo].[messages_users] WITH NOCHECK
    ADD CONSTRAINT [FK_messages_users_message] FOREIGN KEY ([message_id]) REFERENCES [dbo].[messages] ([message_id]);


GO
-- Refactoring step to update target server with deployed transaction logs

IF OBJECT_ID(N'dbo.__RefactorLog') IS NULL
BEGIN
    CREATE TABLE [dbo].[__RefactorLog] (OperationKey UNIQUEIDENTIFIER NOT NULL PRIMARY KEY)
    EXEC sp_addextendedproperty N'microsoft_database_tools_support', N'refactoring log', N'schema', N'dbo', N'table', N'__RefactorLog'
END
GO
IF NOT EXISTS (SELECT OperationKey FROM [dbo].[__RefactorLog] WHERE OperationKey = '6c803c3e-6560-4862-8c0c-07e837507478')
INSERT INTO [dbo].[__RefactorLog] (OperationKey) values ('6c803c3e-6560-4862-8c0c-07e837507478')
IF NOT EXISTS (SELECT OperationKey FROM [dbo].[__RefactorLog] WHERE OperationKey = 'e6dd91b3-53bf-4a90-b005-4f11616b9ce3')
INSERT INTO [dbo].[__RefactorLog] (OperationKey) values ('e6dd91b3-53bf-4a90-b005-4f11616b9ce3')
IF NOT EXISTS (SELECT OperationKey FROM [dbo].[__RefactorLog] WHERE OperationKey = 'd749da46-c668-482c-9c1a-d739df8c8438')
INSERT INTO [dbo].[__RefactorLog] (OperationKey) values ('d749da46-c668-482c-9c1a-d739df8c8438')
IF NOT EXISTS (SELECT OperationKey FROM [dbo].[__RefactorLog] WHERE OperationKey = '0fd29cdd-50e4-4e0b-ae4a-2076ec2e92d7')
INSERT INTO [dbo].[__RefactorLog] (OperationKey) values ('0fd29cdd-50e4-4e0b-ae4a-2076ec2e92d7')

GO

GO
PRINT N'Checking existing data against newly created constraints';


GO
USE [$(DatabaseName)];


GO
ALTER TABLE [dbo].[contacts] WITH CHECK CHECK CONSTRAINT [FK_contacts_user];

ALTER TABLE [dbo].[contacts] WITH CHECK CHECK CONSTRAINT [FK_contacts_contact];

ALTER TABLE [dbo].[messages] WITH CHECK CHECK CONSTRAINT [FK_messages_author];

ALTER TABLE [dbo].[messages_users] WITH CHECK CHECK CONSTRAINT [FK_messages_users_user];

ALTER TABLE [dbo].[messages_users] WITH CHECK CHECK CONSTRAINT [FK_messages_users_message];


GO
PRINT N'Update complete.';


GO
