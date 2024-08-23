package br.edu.ufersa.utils;

public enum ServicePorts {
    GATEWAY_PORT(60000), AUTH_PORT(60050), DEALER_PORT(60100), SESSION_PORT(60150), DATABASE_PORT(60200);

    private final int value;
    private ServicePorts(int value) { this.value = value; }

    public int getValue() {return this.value; }
}
