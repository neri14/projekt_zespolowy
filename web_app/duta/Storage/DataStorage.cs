using duta.Storage.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace duta.Storage
{
    public abstract class DataStorage
    {
        public abstract User GetUser(int user_id);
        public abstract User GetUser(string login);
        public abstract List<string> GetUsersWithLoginInContactList(string login);
        public abstract int CreateUser(string login, string password);

        public abstract List<Message> GetMessagesSince(int user, DateTime time);
        public abstract void AddMessage(DateTime time, List<int> users, int author, string message);
    }

    public static class DataStorageCreator
    {
        private static DataStorage instance;

        public static DataStorage Create<T>() where T : DataStorage, new()
        {
            instance = new T();
            return instance;
        }

        public static DataStorage Get()
        {
            if (instance == null)
            {
                return Create<InternalDataStorage>();
            }
            return instance;
        }

        public static void Clear()
        {
            instance = null;
        }
    }

    public class UserAlreadyExistsException : System.Exception
    {}

    public class UserNotExistingException : System.Exception
    {}
}