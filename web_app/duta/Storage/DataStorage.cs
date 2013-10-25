using duta.Storage.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace duta.Storage
{
    public abstract class DataStorage
    {
        public abstract User GetUser(string login);
        public abstract int CreateUser(string login, string password);
    }

    public static class DataStorageCreator
    {
        private static DataStorage instance;

        public static DataStorage create<T>() where T : DataStorage, new()
        {
            instance = new T();
            return instance;
        }

        public static DataStorage get()
        {
            return instance;
        }

        public static void clear()
        {
            instance = null;
        }
    }

    public class UserAlreadyExistsException : System.Exception
    {
    }
}