using duta.Debug;
using duta.Storage;
using duta.Storage.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Web;
using System.Web.Security;

namespace duta.Managers
{
    public static class UserManager
    {
        private static DataStorage data = DataStorageCreator.Get();

        public static int? CreateUser(string login, string password)
        {
            if (data.GetUser(login) == null)
                return data.CreateUser(login, password);
            return null;
        }

        public static User GetUser(string login)
        {
            return data.GetUser(login);
        }

        public static User GetUser(int user_id)
        {
            return data.GetUser(user_id);
        }

        public static bool Login(string login, string password, string session)
        {
            User u = data.GetUser(login);

            if (u != null && u.password == password)
            {
                if (!Ping(login, session))
                    return false;

                FormsAuthentication.SetAuthCookie(login, false);
                return true;
            }
            return false;
        }

        public static void Logout(string login)
        {
            lock (pingData)
            {
                pingData.RemoveAll(p => p.login == login);
            }
            FormsAuthentication.SignOut();
        }

        static Dictionary<string,TaskCompletionSource<bool>> awaitingStatusUpdates
            = new Dictionary<string,TaskCompletionSource<bool>>();
        public static async Task<List<User>> GetStatusUpdate(string login, DateTime lastUpdate)
        {
            lock(awaitingStatusUpdates)
            {
                awaitingStatusUpdates.Remove(login);
            }
            {
                List<User> changed = GetChangedStatuses(login, lastUpdate);
                if (changed.Count > 0)
                    return changed;
            }

            TaskCompletionSource<bool> task;
            lock(awaitingStatusUpdates)
            {
                task = new TaskCompletionSource<bool>();
                awaitingStatusUpdates.Add(login, task);
            }

            await task.Task;

            lock (awaitingStatusUpdates)
            {
                task = null;
                awaitingStatusUpdates.Remove(login);
            }
            return GetChangedStatuses(login, lastUpdate);
        }

        public static void SetStatus(string login, EUserStatus status, string description)
        {
            //User u = GetUser(login);
            //u.status = status;
            //u.descripton = description;
            //u.last_status_update = DateTime.UtcNow;
            data.SetStatus(login, status, description);

            lock (awaitingStatusUpdates)
            {
                foreach (string notify in data.GetUsersWithLoginInContactList(login))
                {
                    if (awaitingStatusUpdates.ContainsKey(notify))
                    {
                        awaitingStatusUpdates.FirstOrDefault(p => p.Key == notify).Value.SetResult(true);
                    }
                }
            }
        }

        public static Dictionary<string, User> GetContactList(User user)
        {
            Dictionary<string, User> contact_list = new Dictionary<string, User>();
            foreach (KeyValuePair<string, int> pair in user.contact_list)
            {
                contact_list[pair.Key] = GetUser(pair.Value);
            }
            return contact_list;
        }

        public static bool AddContact(string login, string contact_login, string contact_nickname)
        {
            return data.AddContact(login, contact_login, contact_nickname);
        }

        public static bool RemoveContact(string login, string contact_login)
        {
            return data.RemoveContact(login, contact_login);
        }

        public static bool UpdateContact(string login, string contact_login, string contact_nickname)
        {
            return data.UpdateContact(login, contact_login, contact_nickname);
        }

        private static List<User> GetChangedStatuses(string login, DateTime lastUpdate)
        {
            User u = data.GetUser(login);

            List<User> changed = new List<User>();
            foreach (KeyValuePair<string, int> pair in u.contact_list)
            {
                User contact = data.GetUser(pair.Value);
                if (contact.last_status_update > lastUpdate)
                {
                    changed.Add(contact);
                }
            }
            //List<User> changed = u.contact_list.Values.Where(c => c.last_status_update > lastUpdate).ToList();
            return changed;
        }

        private class UserPingData
        {
            public string login { get; set; }
            public string session { get; set; }
            public DateTime timestamp { get; set; }
        };
        private static List<UserPingData> pingData = new List<UserPingData>();
        private static TimeSpan TIMEOUT = new TimeSpan(0,0,5);

        public static bool Ping(string login, string session)
        {
            //return true;
            LoggerWrapper logger = new LoggerWrapper("USR_MNGR");

            lock(pingData)
            {
                UserPingData ping = pingData.FirstOrDefault(p => p.login == login);

                bool userLoggedIn = ping != null;
                bool userSessionCorrect = ping != null && session == ping.session;
                bool userTimedOut = ping != null && DateTime.UtcNow - ping.timestamp > TIMEOUT;

                logger.Log("userLoggedIn " + (userLoggedIn ? "T" : "F"));
                logger.Log("userSessionCorrect " + (userSessionCorrect ? "T" : "F"));
                logger.Log("userTimedOut " + (userTimedOut ? "T" : "F"));

                logger.Log("if statement " + ((userLoggedIn && !userSessionCorrect && !userTimedOut) ? "T" : "F"));

                if (userLoggedIn && !userSessionCorrect && !userTimedOut)
                {
                    logger.Log("UserManager.Ping returns false");
                    return false;
                }

                pingData.RemoveAll(p => p.login == login);
                pingData.Add(new UserPingData()
                {
                    login = login,
                    session = session,
                    timestamp = DateTime.UtcNow
                });
            }

            logger.Log("UserManager.Ping returns true");
            return true;
        }
    }
}