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
        private long last_message_id = 0;
        private List<User> users;
        private List<Message> messages;

        public InternalDataStorage()
        {
            users = new List<User>();
            messages = new List<Message>();

            //test data
            CreateUser("user_a", "pass");
            CreateUser("user_b", "pass");
            CreateUser("user_c", "pass");
            CreateUser("user_d", "pass");
            CreateUser("user_e", "pass");

            User user_a = GetUser("user_a");
            User user_b = GetUser("user_b");
            User user_c = GetUser("user_c");
            User user_d = GetUser("user_d");
            User user_e = GetUser("user_e");

            user_a.contact_list.Add("nick_b", user_b.user_id);
            user_a.contact_list.Add("nick_c", user_c.user_id);
            user_a.contact_list.Add("nick_d", user_d.user_id);
            user_a.contact_list.Add("nick_e", user_e.user_id);

            user_b.contact_list.Add("nick_a", user_a.user_id);
            user_b.contact_list.Add("nick_c", user_c.user_id);
            user_b.contact_list.Add("nick_d", user_d.user_id);
            user_b.contact_list.Add("nick_e", user_e.user_id);
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
                List<string> logins = new List<string>();
                int id = GetUser(login).user_id;

                foreach (User user in users)
                {
                    if (user.contact_list.Values.Contains(id))
                    {
                        logins.Add(user.login);
                    }
                }

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

        public override List<Message> GetMessagesSince(int user, DateTime time)
        {
            lock (messages)
            {
                return messages.Where(m => m.users.Contains(user) && m.author != user && m.time >= time).ToList();
            }
        }

        public override void AddMessage(DateTime time, List<int> msg_users, int author, string message)
        {
            lock (messages)
            {
                lock (users)
                {
                    if (users.FirstOrDefault(u => u.user_id == author) == null)
                    {
                        throw new UserNotExistingException();
                    }
                    foreach (int id in msg_users)
                    {
                        if (users.FirstOrDefault(u => u.user_id == id) == null)
                        {
                            throw new UserNotExistingException();
                        }
                    }
                }

                messages.Add(new Message(++last_message_id, time, msg_users, author, message));
            }
        }

        public override DateTime GetLastMessageUpdate(int user_id)
        {
            lock (users)
            {
                User user = users.FirstOrDefault(u => u.user_id == user_id);
                if (user == null)
                {
                    throw new UserNotExistingException();
                }
                return user.last_messages_download;
            }
        }

        public override void SetLastMessageUpdate(int user_id, DateTime time)
        {
            lock (users)
            {
                User user = users.FirstOrDefault(u => u.user_id == user_id);
                if (user == null)
                {
                    throw new UserNotExistingException();
                }
                user.last_messages_download = time;
            }
        }

        public override List<Message> GetArchive(DateTime from, DateTime to, List<string> usernames)
        {
            return new List<Message>();
        }

        public override bool AddContact(string login, string contact_login, string contact_nickname)
        {
            return false;
        }

        public override bool RemoveContact(string login, string contact_login)
        {
            return false;
        }

        public override bool UpdateContact(string login, string contact_login, string contact_nickname)
        {
            return false;
        }
    }
}