using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Windows.Threading;

namespace duta_deskopt
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private duta_deskopt.MessangerBox box;
        private List<GetContactListResult> contactList;
        private DutaServices ds;
        private LoginResult me;
        private UserDataResponse userData;

        private Dictionary<int, Ellipse> allStates = new Dictionary<int,Ellipse>();
        private Dictionary<int, Label> AllDescription = new Dictionary<int,Label>();
        private Dictionary<int, CheckBox> AllChecks = new Dictionary<int, CheckBox>();

        public MainWindow(String login, String password)
        {
            InitializeComponent();
            
            ds = new DutaServices(this);
            me = ds.Login(login, password);
            App.ds = ds;
            if (me.logged_in == 1)
            {
                myState.SelectedIndex = 0;
                contactList = ds.getContactList();
                generateContactList(contactList);
                userData = ds.getUserData(me.user_id);
                description.Text = userData.description;

                Thread trd = new Thread(new ThreadStart(this.messageThread));
                trd.IsBackground = true;
                trd.Start();

                Thread trd_state = new Thread(new ThreadStart(this.statusThread));
                trd_state.IsBackground = true;
                trd_state.Start();
            }
            else 
            {
 
            }
            
        }

        private void statusThread()
        {
            while (true) 
            {
                List<GetStatusUpdateResponse_User> response = ds.getStatusUpdate();
                if (response != null) {
                    this.Dispatcher.BeginInvoke(DispatcherPriority.Normal, (ThreadStart)delegate()
                    {
                        updateContactList(response);
                    }
                    );
                }
            }
        }

        private void messageThread()
        {

            while (true)
            {
                List<GetMessageResponse> messanges = ds.getMessange();
                if (messanges != null)
                {
                    if (messanges.Count > 0)
                    {
                        foreach (GetMessageResponse messange in messanges)
                        {
                            this.Dispatcher.BeginInvoke(DispatcherPriority.Normal, (ThreadStart)delegate()
                            {
                                String userName = "";
                                DateTime origin = new DateTime(1970, 1, 1, 0, 0, 0, 0);
                                origin = origin.AddMilliseconds(messange.timestamp);                             
                                String time = origin.ToShortTimeString();

                                if (messange.users.Count > 2)
                                {
                                    /*
                                     * Konferencja
                                     */
                                    List<int> users = messange.users;
                                    users.Remove(me.user_id);
                                    String author = this.getAuthorNick(messange.author);

                                    if (box == null)
                                    {

                                        box = new MessangerBox("Confferention", contactList, ds, me.user_id, users);
                                        box.Show();
                                        userName = box.findConfferention(users);

                                        box.addMessange(messange.message, userName, time, author);
                                    }
                                    else if (box != null && box.isConferention(users))
                                    {
                                        userName = box.findConfferention(users);
                                        box.addMessange(messange.message, userName, time, author);
                                    }
                                    else
                                    {
                                        box.newTab("Confferention", users);
                                        userName = box.findConfferention(users);
                                        box.addMessange(messange.message, userName, time, author);
                                    }
                                }
                                else
                                {
                                    for (int i = 0; i < contactList.Count(); i++)
                                    {
                                        if (contactList[i].user_id == messange.author)
                                        {
                                            userName = contactList[i].nickname;
                                        }
                                    }
                                    if (box == null)
                                    {
                                        box = new MessangerBox(userName, contactList, ds, me.user_id);
                                        box.Show();

                                        box.addMessange(messange.message, userName, time);
                                    }
                                    else if (box.isTab(userName))
                                    {
                                        box.addMessange(messange.message, userName, time);
                                    }
                                    else
                                    {
                                        box.newTab(userName);
                                        box.addMessange(messange.message, userName, time);
                                    }
                                }

                            }
                            );
                        }
                    }
                }          
            }
        }

        private String getAuthorNick(int id)
        {
            foreach (GetContactListResult contact in contactList) {
                if (contact.user_id == id) {
                    return contact.nickname;
                }
            }
            return id.ToString();
        }

        private void updateContactList(List<GetStatusUpdateResponse_User> response) 
        {
            int id = -1;
            foreach (GetStatusUpdateResponse_User user in response)
            {
                id = user.user_id;
                switch (user.status)
                {
                    case 0:
                        allStates[id].Fill = new SolidColorBrush(System.Windows.Media.Colors.Green);
                        break;
                    case 1:
                        allStates[id].Fill = new SolidColorBrush(System.Windows.Media.Colors.Blue);
                        break;
                    case 2:
                        allStates[id].Fill = new SolidColorBrush(System.Windows.Media.Colors.Red);
                        break;
                }

                AllDescription[id].Content = user.description;
            }
        }

        private void generateContactList(List<GetContactListResult> contactList) 
        {
            if (contactList != null)
            {
                foreach (GetContactListResult contact in contactList)
                {
                    contacts.Items.Add(generateContact(contact));
                }      
            }
                
        }

        private ListBoxItem generateContact(GetContactListResult contact)
        {
            ListBoxItem item = new ListBoxItem();
            Grid grid = new Grid();

            grid.RowDefinitions.Add(new RowDefinition { Height = new GridLength(1, GridUnitType.Star) });
            grid.RowDefinitions.Add(new RowDefinition { Height = new GridLength(1, GridUnitType.Star) });
            grid.ColumnDefinitions.Add(new ColumnDefinition { Width = new GridLength(1, GridUnitType.Star) });
            grid.ColumnDefinitions.Add(new ColumnDefinition { Width = new GridLength(1, GridUnitType.Star) });

            item.Selected += new RoutedEventHandler(item_Selected);
            item.Name = contact.nickname;

            Ellipse state = new Ellipse();
            state.Width = 15;
            state.Height = 15;
            switch (contact.status) {
                case 0:
                    state.Fill = new SolidColorBrush(System.Windows.Media.Colors.Green);
                    break;
                case 1:
                    state.Fill = new SolidColorBrush(System.Windows.Media.Colors.Blue);
                    break;
                case 2:
                    state.Fill = new SolidColorBrush(System.Windows.Media.Colors.Red);
                    break;
            }
            
            Grid.SetColumn(state, 0);
            //Grid.SetRowSpan(state, 2);
            grid.Children.Add(state);
            allStates.Add(contact.user_id,state);

            Label user = new Label();
            user.Content = contact.nickname;
            Grid.SetColumn(user, 1);
            grid.Children.Add(user);

            Label desc = new Label();
            desc.Content = contact.description;
            Grid.SetColumn(desc, 1);
            Grid.SetRow(desc, 1);
            AllDescription.Add(contact.user_id,desc);            
            grid.Children.Add(desc);

            CheckBox cb = new CheckBox();
            cb.Name = "id_" + contact.user_id;
            Grid.SetRow(cb, 1);
            grid.Children.Add(cb);
            AllChecks.Add(contact.user_id, cb);

            item.Content = grid;
            return item;
        }

        private void item_Selected(object sender, RoutedEventArgs e)
        {
            ListBoxItem item = (ListBoxItem)sender;
            if (box == null)
            {
                box = new MessangerBox(item.Name.ToString(), contactList,ds,me.user_id);
                box.Show();
            }
            else {
                box.newTab(item.Name.ToString());
               
            }
            
            
        }

        public void descChange_Click(object sender, RoutedEventArgs e)
        {
            ComboBoxItem state = (ComboBoxItem)myState.SelectedItem;
            int idState = Convert.ToInt32((string)state.Tag);
            ds.setStatus(idState, description.Text);
        }

        public void conf_Click(object sender, RoutedEventArgs e)
        {
            List<int> users_id = new List<int>();
            foreach (KeyValuePair<int, CheckBox> pair in AllChecks)
            {
                CheckBox cb = pair.Value;
                if (cb.IsChecked == true) 
                {
                    users_id.Add((int)pair.Key);
                }
                
            }
            if (users_id.Count>0) 
            {
                if (box == null)
                {
                    box = new MessangerBox("Confferention", contactList, ds, me.user_id,users_id);
                    box.Show();
                }
                else if(box!=null && !box.isConferention(users_id))
                {
                    box.newTab("Confferention", users_id);
                }
            }
        }
    }   
}
