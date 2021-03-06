package qu.cms.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.google.gson.Gson;

import qu.cms.entity.Contact;
import qu.utils.Utils;

public class ContactDBRepository implements IContactRepository {
	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("CmsDbPu");
	private EntityManager em;

	public ContactDBRepository() {
		em = entityManagerFactory.createEntityManager();
		insertTestData();
	}
    
	public List<Contact> getContacts() {
		Query query = em.createQuery("select c from Contact as c");
		return query.getResultList();
    }

    public Contact addContact(Contact contact) {
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.persist(contact);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			throw e;
		}
		return contact;
    }
    
    public void updateContact(Contact contact) {
    	EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.merge(contact);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			throw e;
		}
    }

    public void deleteContact(int contactId) {
    	Query query = em.createQuery("delete from Contact where id = :contactId");
    	query.setParameter("contactId", contactId);

    	EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			query.executeUpdate(); 
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			throw e;
		}
    }
        
    public Contact getContact(int id) {
        return em.find(Contact.class, id);
    }

    public int getContactsCount() {
        return ((Long) em.createQuery("select count(c) from Contact as c").getSingleResult()).intValue();
    }

   
    public void insertTestData() {
       if (getContactsCount() > 0)
			return;
		
        Gson gson = new Gson();
        String contactsStr = Utils.readUrl(Utils.contactsUrl);
        System.out.println(contactsStr);

        Contact[] contactArray = gson.fromJson(contactsStr, Contact[].class);
        //Convert the array to a editable list 
        List<Contact> contacts = new ArrayList<>(Arrays.asList(contactArray));

        contacts.forEach(contact -> addContact(contact));
    }

    public List<String> getCountries() {
    	return Utils.getCountries();
    }
    
    public List<String> getCities(String country) {
    	return Utils.getCities(country);
    }  
}
