## Prática Offline 1 - Loja de Carros

Este projeto, desenvolvido na disciplina de Programação Distribuída da UFERSA, tem como objetivo adaptar o um projeto antigo pôr em pratica o conteúdo passado em aula sobre tecnologias vistas na disciplina - nesse caso o uso de *lambda expression*, serviço de executores e com a simulação de três algoritmos de balanceamento de carga das requisições.


### Interpretação do problema

O objetivo do projeto é fazer uma simulação de um sistema cliente/servidor para uma loja de carros, no qual é possível consultar carros anteriormente utilizados e seus dados.

### Especificações do sistema

- Todo carro deve possuir:
    -  Uma **categoria**, podendo ser econômico, intermediário ou executivo;
    - Renavam;
    - Nome do carro;
    - Ano de fabricação;
    - Quantidade disponível;
    - Preço;

- O sistema deve ter/permitir:

    - **Autenticação**: usando um login e uma senha,  o usuário pode entrar no sistema.
    - **Adição de carros**: um usuário autenticado pode adicionar carros ao sistema, fornecendo todos os atributos - a quantidade disponível é atualizada.
    - **Pesquisa de carro**: um usuário autenticado pode realizar uma busca de um carro no sistema por nome ou renavam.
    - **Atualização de carros (atributos)**: um usuário autenticado pode alterar os atributos de um carro adicionado.
    - **Remoção de carros**: um usuário autenticado pode, à partir do renavam do carro, remover um registro de carro, junto a todos os atributos. (Também ocorre quando a quantidade chegar a zero)
    - **Listagem de carros**: um usuário autenticado pode listar os carros da loja, com todos os atributos, por categoria ou de forma geral. (Apresentado em ordem alfabética)
    - **Atualização de listagens em "tempo real"**: quando um carro ocorrer uma adição, remoção ou atualização dos carros e seus atributos, o sistema deve reenviar uma listagem atualizada aos clientes.
    - **Consulta de estoque**: um usuário autenticado pode consultar a quantidade de carros armazenados.
    - **Compra de carro**: um usuário autenticado pode, após uma consulta e análise do preço, efetuar a compra de um carro.

#### Detalhes dos requisitos

- Deve fazaer uso de técnicas de sincronização, replicação, tolerância a falhas e segurança
- Base de dados deve estar separado em outro processo e estar ligado diretamente    

### Tecnologias utilizadas
- Visual Studio Code
- Java SE **21**