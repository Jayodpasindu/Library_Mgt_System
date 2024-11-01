package com.example.library_managmenrt.dao;

import com.example.library_managmenrt.model.Member;
import com.example.library_managmenrt.utill.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import java.util.List;

public class TransactionDao
{
    public void saveTransaction(com.example.library_managmenrt.model.Transaction bookTransaction){
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(bookTransaction);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public boolean updateTransactionDetails(com.example.library_managmenrt.model.Transaction bookTransaction) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Update the member object in the session
            session.update(bookTransaction);

            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    public List<com.example.library_managmenrt.model.Transaction> getAllTransactions() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery("FROM Transaction");
            return query.getResultList();
        }
    }

    public boolean deleteTransaction(com.example.library_managmenrt.model.Transaction bookTransaction) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(bookTransaction);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                return false;
            }
            e.printStackTrace();
        }
        return true;
    }
}
