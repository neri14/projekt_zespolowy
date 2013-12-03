package com.dutamobile.util;

import com.dutamobile.model.Contact;
import com.dutamobile.model.Message;
import com.dutamobile.model.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Bartosz on 01.12.13.
 */

//TODO klasę usunąć w ostatecznej wersji
public class Test
{

    public static List<Message> generateConversation(String name, int id)
    {
        Random r = new Random();

        String [] mgs = new String []
                {
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut augue eros, ullamcorper id mauris a, lacinia ultricies purus. Phasellus ligula enim, fringilla vitae elit eget, consequat gravida felis. Vivamus sem elit, semper eu rhoncus tristique, porta eget metus. Aliquam erat volutpat. Etiam vel eros vitae sapien ultricies blandit vitae convallis orci. Praesent laoreet ante quis ligula fermentum sodales. Praesent adipiscing lacus in metus tristique, et imperdiet purus eleifend. Vivamus fringilla commodo velit.",
                        "Ohh cool",
                        "Hi! Don't believe man, I just get new job!",
                        "Hi, what's up?"
                };

        List<Message> data = new ArrayList<Message>();
        int []  l = new int [] {Helper.MyID, id };

        Message m = new Message(name, l);
        m.setAuthor(id);
        m.setTimestamp(System.currentTimeMillis());
        data.add(m);

        for(int i = 0 ; i < 4 ; i++)
        {
            m = new Message();
            m.setAuthor(i%2 == 0 ? Helper.MyID : id);
            m.setTimestamp(System.currentTimeMillis());
            m.setMessageText(mgs[r.nextInt(4)]);
            data.add(m);
        }

        return data;
    }

    public static List<Contact> generateContacts()
    {
        List<Contact> contacts = new ArrayList<Contact>();
        int counter = -1;

        Contact c = new Contact();
        c.setId(++counter);
        c.setLogin("john12");
        c.setName("John");
        c.setDescription("Cool men!");
        c.setStatus(Status.AWAY);
        c.setMessages(generateConversation(c.getName(), counter));
        contacts.add(c);

        c = new Contact();
        c.setId(++counter);
        c.setName("Marie");
        c.setLogin("cuntMarie");
        c.setDescription("I just bought new shoes!");
        c.setStatus(Status.AVAILABLE);
        c.setMessages(generateConversation(c.getName(), counter));
        contacts.add(c);

        c = new Contact();
        c.setId(++counter);
        c.setName("Alice");
        c.setLogin("junkiegirl14");
        c.setDescription("Fucking rabbit!");
        c.setStatus(Status.BUSY);
        c.setMessages(generateConversation(c.getName(), counter));
        contacts.add(c);

        return contacts;
    }
}
