using duta.DatabaseModel;
using duta.Storage.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace duta.Storage
{
    public class ExternalDataStorage : DataStorage
    {
        public override User GetUser(int user_id)
        {
            using (DataEntities ctx = new DataEntities())
            {
                return Convert(ctx.users.FirstOrDefault(u => u.user_id == user_id));
            }
        }

        public override User GetUser(string login)
        {
            using (DataEntities ctx = new DataEntities())
            {
                return Convert(ctx.users.FirstOrDefault(u => u.login == login));
            }
        }

        public override List<string> GetUsersWithLoginInContactList(string login)
        {
            using (DataEntities ctx = new DataEntities())
            {
                return ctx.users.Where(u => u.contacts.FirstOrDefault(c => c.contacting_user.login == login) != null).Select(s => s.login).ToList();
            }
        }

        public override int CreateUser(string login, string password)
        {
            using (DataEntities ctx = new DataEntities())
            {
                if (ctx.users.FirstOrDefault(u => u.login == login) == null)
                {
                    user new_user = new user()
                    {
                        login = login,
                        password = password,
                        status = 0,
                        description = "",
                        last_status_update = DateTime.Now
                    };
                    ctx.users.Add(new_user);
                    ctx.SaveChanges();

                    return ctx.users.FirstOrDefault(u => u.login == login).user_id;
                }

                throw new UserAlreadyExistsException();
            }
        }

        public override List<Message> GetMessagesSince(int user, DateTime time)
        {
            using (DataEntities ctx = new DataEntities())
            {
                List<Message> msgs = new List<Message>();
                foreach (message msg in ctx.messages.Where(m => m.users.FirstOrDefault(u => u.user_id == user) != null && m.time >= time))
                {
                    msgs.Add(Convert(msg));
                }
                return msgs;
            }
        }

        public override void AddMessage(DateTime time, List<int> users, int author, string message)
        {
            using (DataEntities ctx = new DataEntities())
            {
                if (ctx.users.FirstOrDefault(u => u.user_id == author) == null)
                {
                    throw new UserNotExistingException();
                }
                foreach (int id in users)
                {
                    if (ctx.users.FirstOrDefault(u => u.user_id == id) == null)
                    {
                        throw new UserNotExistingException();
                    }
                }

                message msg = new message
                {
                    author_id = author,
                    time = time,
                    message1 = message
                };

                foreach (int id in users)
                {
                    msg.users.Add(ctx.users.FirstOrDefault(u => u.user_id == id));
                }

                ctx.messages.Add(msg);
                ctx.SaveChanges();
            }
        }

        private User Convert(user u)
        {
            User entity = new User(u.user_id, u.login, u.password)
            {
                descripton = u.description,
                last_status_update = u.last_status_update,
                status = (EUserStatus)u.status,
                contact_list = new Dictionary<string,int>()
            };

            foreach (contact contact in u.contacts)
            {
                entity.contact_list[contact.name] = contact.contact_id;
            }

            return entity;
        }

        private Message Convert(message m)
        {
            return new Message(m.message_id, m.time, m.users.Select(u => u.user_id).ToList(), m.author_id, m.message1);
        }
    }
}