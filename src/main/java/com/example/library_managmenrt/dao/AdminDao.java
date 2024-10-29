package com.example.library_managmenrt.dao;

import com.example.library_managmenrt.model.Admin;
import com.example.library_managmenrt.utill.HibernateUtil;
import com.example.library_managmenrt.utill.PersistenceManager;
import org.hibernate.Session;
import org.hibernate.Transaction;
import javax.persistence.EntityTransaction;

public class AdminDao {
    public Admin getAdminByUserName(String userName){
        Transaction transaction = null;
        Admin admin = null;

        try{
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();
            admin = session.createQuery("FROM Admin WHERE username = :username", Admin.class)
                    .setParameter("username", userName)
                    .uniqueResult();
            transaction.commit();
        }catch (Exception e){
            System.out.println(e);
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return  admin;
    }
}
