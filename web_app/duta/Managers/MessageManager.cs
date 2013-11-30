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
        private static Dictionary<int, List<long>> sent = new Dictionary<int, List<long>>();

        private static Object locker = new Object();

        public static async Task<List<Message>> GetMessageUpdate(int user, DateTime lastUpdate)
        {
            lock (locker)
            {
                awaitingMessageUpdates.Remove(user);
            }

            List<Message> messages = data.GetMessagesSince(user, lastUpdate);
            messages = RemoveRecentlySentMessages(messages, user);
            if (messages.Count > 0)
            {
                sent[user] = messages.Select(m => m.message_id).ToList();
                return messages;
            }

            TaskCompletionSource<bool> task;
            lock (locker)
            {
                task = new TaskCompletionSource<bool>();
                awaitingMessageUpdates.Add(user, task);
            }

            await task.Task;

            lock (locker)
            {
                task = null;
                awaitingMessageUpdates.Remove(user);
            }

            messages = data.GetMessagesSince(user, lastUpdate);
            messages = RemoveRecentlySentMessages(messages, user);
            sent[user] = messages.Select(m => m.message_id).ToList();
            return messages;
        }

        private static List<Message> RemoveRecentlySentMessages(List<Message> msgs, int user)
        {
            logger.Log(user + ": Messages to send " + ListOut(msgs.Select(m => m.message_id).ToList()));

            List<Message> result = msgs;

            if (sent.ContainsKey(user))
            {
                logger.Log(user + ": Already sent " + ListOut(sent[user]));
                result = msgs.Where(m => !sent[user].Contains(m.message_id)).ToList();
            }

            logger.Log(user + ": After removing " + ListOut(result.Select(m => m.message_id).ToList()));
            return result;
        }

        private static string ListOut(List<long> list)
        {
            string str = "";
            foreach (long l in list)
                str += l + ", ";
            return str;
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