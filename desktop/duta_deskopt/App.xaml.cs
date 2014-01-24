using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Threading.Tasks;
using System.Windows;

namespace duta_deskopt
{
    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
        public static DutaServices ds;
        void App_Exit(object sender, ExitEventArgs e)
        {
            if (ds != null)
            {
                ds.Logout();
            }
        }
    }
}
