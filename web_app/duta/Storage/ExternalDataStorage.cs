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
                return ctx.users.Where(u => u.contacts.FirstOrDefault(c => c.contact_.login == login) != null).Select(s => s.login).ToList();
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
                        last_status_update = DateTime.Now,
                        last_messages_download = DateTime.Now
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
                foreach (message msg in ctx.messages.Where(m => m.author_id != user && m.users.FirstOrDefault(u => u.user_id == user) != null && m.time >= time))
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

        public override DateTime GetLastMessageUpdate(int user_id)
        {
            using (DataEntities ctx = new DataEntities())
            {
                user usr = ctx.users.FirstOrDefault(u => u.user_id == user_id);
                if (usr == null)
                {
                    throw new UserNotExistingException();
                }
                return usr.last_messages_download;
            }
        }

        public override void SetLastMessageUpdate(int user_id, DateTime time)
        {
            using (DataEntities ctx = new DataEntities())
            {
                user usr = ctx.users.FirstOrDefault(u => u.user_id == user_id);
                if (usr == null)
                {
                    throw new UserNotExistingException();
                }
                usr.last_messages_download = time;
                ctx.SaveChanges();
            }
        }

        private bool ContainsAllOf(List<int> list_a, List<int> list_b)
        {
            foreach (int id in list_b)
            {
                if (!list_a.Contains(id))
                    return false;
            }
            return true;
        }

        public override List<Message> GetArchive(DateTime from, DateTime to, List<int> ids)
        {
            using (DataEntities ctx = new DataEntities())
            {
                List<message> msgs = ctx.messages.Where(m => m.users.Select(u => u.user_id).Intersect(ids).Count() == ids.Count()).Where(m2 => m2.time >= from && m2.time <= to).ToList();
                return Convert(msgs);
            }
        }

        public override bool AddContact(string login, string contact_login, string contact_nickname)
        {
            using (DataEntities ctx = new DataEntities())
            {
                user usr = ctx.users.FirstOrDefault(u => u.login == login);
                user usr_ct = ctx.users.FirstOrDefault(u => u.login == contact_login);

                if (usr == null || usr_ct == null)
                    return false;

                if (ctx.contacts.FirstOrDefault(u => u.user_id == usr.user_id && u.contact_id == usr.user_id) != null)
                    return false;

                contact ct = new contact()
                {
                    user_id = usr.user_id,
                    contact_id = usr_ct.user_id,
                    name = contact_nickname
                };

                ctx.contacts.Add(ct);
                ctx.SaveChanges();
                return true;
            }
        }

        public override bool RemoveContact(string login, string contact_login)
        {
            using (DataEntities ctx = new DataEntities())
            {
                user usr = ctx.users.FirstOrDefault(u => u.login == login);
                user usr_ct = ctx.users.FirstOrDefault(u => u.login == contact_login);

                if (usr == null || usr_ct == null)
                    return false;

                contact ct = ctx.contacts.FirstOrDefault(u => u.user_id == usr.user_id && u.contact_id == usr_ct.user_id);

                if (ct == null)
                    return false;

                ctx.contacts.Remove(ct);
                ctx.SaveChanges();
                return true;
            }
        }

        public override bool UpdateContact(string login, string contact_login, string contact_nickname)
        {
            using (DataEntities ctx = new DataEntities())
            {
                user usr = ctx.users.FirstOrDefault(u => u.login == login);
                user usr_ct = ctx.users.FirstOrDefault(u => u.login == contact_login);

                if (usr == null || usr_ct == null)
                    return false;

                contact ct = ctx.contacts.FirstOrDefault(u => u.user_id == usr.user_id && u.contact_id == usr.user_id);

                if (ct == null)
                    return false;

                ct.name = contact_nickname;
                ctx.SaveChanges();
                return true;
            }
        }

        public override bool SetStatus(string login, EUserStatus status, string description)
        {
            using (DataEntities ctx = new DataEntities())
            {
                user usr = ctx.users.FirstOrDefault(u => u.login == login);

                if (usr == null)
                    return false;

                usr.status = (int)status;
                usr.description = description;
                usr.last_status_update = DateTime.Now;

                ctx.SaveChanges();
                return true;
            }
        }

        private User Convert(user u)
        {
            if (u == null)
                return null;

            User entity = new User(u.user_id, u.login, u.password)
            {
                descripton = u.description,
                last_status_update = u.last_status_update,
                last_messages_download = u.last_messages_download,
                status = (EUserStatus)u.status,
                contact_list = new Dictionary<string,int>()
            };

            foreach (contact contact in u.contacts)
            {
                entity.contact_list[contact.name] = contact.contact_id;
            }

            return entity;
        }

        private List<Message> Convert(List<message> msgs)
        {
            List<Message> converted = new List<Message>();

            foreach(message m in msgs)
            {
                converted.Add(Convert(m));
            }

            return converted;
        }

        private Message Convert(message m)
        {
            return new Message(m.message_id, m.time, m.users.Select(u => u.user_id).ToList(), m.author_id, m.message1);
        }
    }
}