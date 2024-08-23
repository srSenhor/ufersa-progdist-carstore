# TODO list

O que tiver riscado e com um ❌ eu já concluí.

## Coisas que eu **preciso** fazer:

### Do trabalho antigo

- [ ] Garantir que o Banco de dados se comunique apenas com o serviço da loja de carros
- [x] Garantir a sincronização entre cópias de serviços concorrentes
- [x] Replicação do serviço de loja de carros e de banco de dados (ao menos 3 cópias)
- [ ] Garantir que o projeto consiga operar em duas máquinas diferentes
- [ ] Fazer com que os resultados de pesquisas do usuário sejam atualizados constantmente
- [ ] (Opcional) Tolerância a falhas para o serviço de loja de carros replicado

### Do trabalho atual

- [x] Implementar o serviço de executores para o gerenciamento de threads
- [x] Usar *lambda expressions* e interfaces funcionais quando for possível e aplicável
- [ ] Simular o balanceamento de carga de requisições com 3 algoritmos dos que foram apresentados no documento
  - [ ] Implementar e testar o *Round Robin*
  - [ ] ~~Implementar e testar o *Consistent Hashing*~~
  - [x] Implementar e testar o *Random*
  - [x] Implementar e testar o *Least Connections*

## Propostas de solução

- Para garantir a sincronização, proponho o uso de Locks do tipo Write & Read e também o uso de Singleton para o Banco de Dados
- ~~Para replicar os serviços, sugiro o uso de um contador interno ou um ID que vai acrescentar na hora de lançar um serviço à porta~~
- Para operar em máquinas diferentes, acho válido usar o WSL pra ilustrar o exemplo, mas vai dar um trabalho botar pra funcionar de novo.
- Para os resultados de pesquisa ficarem sendo constantemente atualizados seria interessante usar uma thread pra cuidar da impressão, mas o desafio maior é imprimir o que o usuário escreve antes dele apertar Enter
