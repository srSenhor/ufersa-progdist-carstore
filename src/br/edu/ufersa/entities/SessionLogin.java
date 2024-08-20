package br.edu.ufersa.entities;

import java.io.Serializable;

import javax.crypto.SecretKey;

import br.edu.ufersa.security.RSAImpl;
import br.edu.ufersa.utils.RSAKey;
import br.edu.ufersa.utils.UserType;

public class SessionLogin implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private String username;
    private UserType type;
    private RSAImpl sessionRSA;
    private RSAKey serverPublicKey;
    private SecretKey aesKey;

    public SessionLogin(String username, UserType type, RSAImpl sessionRSA, RSAKey serverPublicKey, SecretKey aesKey) {
        this.username = username;
        this.type = type;

        setAeSKey(aesKey);
        setServerPublicKey(serverPublicKey);
        setSessionRSA(sessionRSA);
    }

    private void setSessionRSA(RSAImpl sessionRSA) {
        if (sessionRSA != null) {
            this.sessionRSA = sessionRSA;
        } else {
            throw new RuntimeException("Erro ao inicializar RSA da sessão");
        }
    }

    public void setServerPublicKey(RSAKey serverPublicKey) {
        if (serverPublicKey != null) {
            this.serverPublicKey = serverPublicKey;
        } else {
            throw new RuntimeException("Erro ao recuperar chave pública do servidor");
        }
    }

    private void setAeSKey(SecretKey aesKey) {
        if (aesKey != null) {
            this.aesKey = aesKey;
        } else {
            throw new RuntimeException("Erro ao inicializar chave do AES da sessão");
        }
    }

    public String getUsername() {
        return username;
    }

    public UserType getType() {
        return type;
    }

    public RSAImpl getSessionRSA() {
        return sessionRSA;
    }

    public RSAKey getServerPublicKey() {
        return serverPublicKey;
    }

    public SecretKey getAesKey() {
        return aesKey;
    }

    // ! Para fins de debug
    @Override
    public String toString() {
        return "SessionLogin [login=" + username + ", sessionRSA=" + sessionRSA + ", serverPuKey=" + serverPublicKey
                + ", aesKey=" + aesKey + "]";
    }





}
