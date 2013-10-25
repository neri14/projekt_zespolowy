using duta.Storage.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace duta.Storage
{
    public class InternalDataStorage : DataStorage
    {
        private int last_user_id = 0;
        private List<User> users;

        public InternalDataStorage()
        {
            users = new List<User>();
        }

        public override User GetUser(string login)
        {
            lock (users)
            {
                return users.FirstOrDefault(u => u.login == login);
            }
        }

        public override int CreateUser(string login, string password)
        {
            lock (users)
            {
                if (GetUser(login) == null)
                {
                    User u = new User(++last_user_id, login, password);
                    users.Add(u);
                    return u.user_id;
                }

                throw new UserAlreadyExistsException();
            }
        }
    }
}