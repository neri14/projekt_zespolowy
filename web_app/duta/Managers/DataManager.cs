using duta.Storage;
using duta.Storage.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace duta.Managers
{
    public class DataManager
    {
        DataStorage data;

        public DataManager(DataStorage data)
        {
            this.data = data;
        }

        public int? CreateUser(string login, string password)
        {
            if (data.GetUser(login) == null)
                return data.CreateUser(login, password);
            return null;
        }

        public User GetUser()
        {
            throw new NotImplementedException();
        }
    }
}