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

            //test data
            CreateUser("user_a", "pass");
            CreateUser("user_b", "pass");

            User user_a = GetUser("user_a");
            User user_b = GetUser("user_b");

            user_a.contact_list.Add("nick_b", user_b);
        }

        public override User GetUser(int user_id)
        {
            lock (users)
            {
                return users.FirstOrDefault(u => u.user_id == user_id);
            }
        }

        public override User GetUser(string login)
        {
            lock (users)
            {
                return users.FirstOrDefault(u => u.login == login);
            }
        }

        public override List<string> GetUsersWithLoginInContactList(string login)
        {
            lock (users)
            {
                List<string> logins = users.Where(u1 =>
                    null != u1.contact_list.Values.FirstOrDefault(u2 => u2.login == login)).
                    Select(u3 => u3.login).ToList();

                return logins;
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