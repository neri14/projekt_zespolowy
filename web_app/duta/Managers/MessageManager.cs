using duta.Debug;
using duta.Storage;
using duta.Storage.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Web;

namespace duta.Managers
{
    public static class MessageManager
    {
        private static LoggerWrapper logger = new LoggerWrapper("MSG_MNGR");
        private static DataStorage data = DataStorageCreator.Get();
        private static Dictionary<int, TaskCompletionSource<bool>> awaitingMessageUpdates
            = new Dictionary<int, TaskCompletionSource<bool>>();

        private static Object locker = new Object();

        public static async Task<List<Message>> GetMessageUpdate(int user, DateTime lastUpdate)
        {
            logger.Log(String.Format("User {0}: GetMessageUpdate enter for user {0} for messages since {1}", user, lastUpdate.ToLongTimeString()));
            lock (locker)
            {
                awaitingMessageUpdates.Remove(user);
            }
            {
                List<Message> changed = data.GetMessagesSince(user, lastUpdate);
                if(changed.Count > 0)
                    return changed;
            }
            logger.Log(String.Format("User {0}: GetMessageUpdate no messages to send immidiatelly found", user));

            TaskCompletionSource<bool> task;
            lock (locker)
            {
                task = new TaskCompletionSource<bool>();
                awaitingMessageUpdates.Add(user, task);
            }

            logger.Log(String.Format("User {0}: GetMessageUpdate waiting begins", user));
            await task.Task;
            logger.Log(String.Format("User {0}: GetMessageUpdate waiting ended", user));

            lock (locker)
            {
                task = null;
                awaitingMessageUpdates.Remove(user);
            }

            logger.Log(String.Format("User {0}: GetMessageUpdate messages ready to return", user));
            return data.GetMessagesSince(user, lastUpdate);
        }

        public static DateTime SendMessage(int sender, List<int> receivers, string message)
        {
            DateTime time = DateTime.Now;
            data.AddMessage(time, receivers, sender, message);

            lock (locker)
            {
                foreach (int u in receivers.Where(r => r != sender))
                {
                    if (awaitingMessageUpdates.ContainsKey(u))
                    {
                        awaitingMessageUpdates.FirstOrDefault(p => p.Key == u).Value.SetResult(true);
                    }
                }
            }
            return time;
        }

        public static DateTime GetLastMessageUpdate(int user_id)
        {
            return data.GetLastMessageUpdate(user_id);
        }

        public static void SetLastMessageUpdate(int user_id, DateTime time)
        {
            data.SetLastMessageUpdate(user_id, time);
        }
    }
}