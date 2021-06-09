# Arquitetura da Solução

## Principais classes

O projeto tem **4** principais classes que serão explicadas logo abaixo: 

#### Classe Main

A classe main tem 2 responsabilidades:

-  Criar os arquivos de diretório e cesto e chamar o construtor da ExtensibleHash para criar os objetos de bucket e diretório e passar o construtor do objeto PersonForMedicalRecord:

  ![image](https://user-images.githubusercontent.com/57811501/121281929-23c6df80-c8af-11eb-9981-9bef0ed3fddb.png)

- Disponibilizar ao usuário opções de "Inserir - Buscar - Excluir - Imprimir - Sair" para manipulação de dados dos prontuários, e direcionar para uma respectiva função que corresponda com o selecionado pelo usuário:
                                           ![image](https://user-images.githubusercontent.com/57811501/121282383-c7b08b00-c8af-11eb-8ec5-5dfa98c07375.png)

  ![image](https://user-images.githubusercontent.com/57811501/121282555-19f1ac00-c8b0-11eb-8115-e2ae45e5107e.png)

#### Interface HashRecord

Responsável por obrigar nas classes que à estenderem a implementação dos métodos de **hashCode()** que utiliza uma chave numérica para ser utilizada no diretório, **size()** utilizado para pegar o tamanho do fixo do registro, **toByteArray**() para converter um respectivo elemento em um vetor de bytes, e o **fromByteArray** para ler de um vetor de bytes.

![image](https://user-images.githubusercontent.com/57811501/121284852-d8fb9680-c8b3-11eb-902e-2362bd87edfa.png)



#### Classe PersonForMedicalRecord

Essa classe é o nosso objeto principal que contem os parâmetros de Nome, data de nascimento, sexo, cpf, id, o tamanho do CPF para ter uma validação do formato de tamanho do mesmo,  um tamanho máximo que é utilizado para escolher o tamanho máximo do registro:

![image](https://user-images.githubusercontent.com/57811501/121285318-8ec6e500-c8b4-11eb-9bb6-7e0734710d61.png)

**Construtor**:

![image](https://user-images.githubusercontent.com/57811501/121285378-b1f19480-c8b4-11eb-93d1-af710c70daa6.png)

Implementação de **hashCode()** para retorno de CPF para o índice, implementação do **size()** para retorno do tamanho máximo do registro, e **toString()** para juntar todos os dados necessários à serem impressos na tela em um formato legível

![image](https://user-images.githubusercontent.com/57811501/121285478-d77e9e00-c8b4-11eb-8245-06d2a06b65e9.png)

Implementação do **toByteArray()** escreve os dados que estão nesse objeto em um vetor de bytes. Os dois a interação entre os dois vetores de bytes é feita para que sempre fique inserido um registro em seu tamanho máximo esperado. Ele cria um vetor de bytes com seu tamanho máximo preenchido por espaços, e insere os dados reais nesse arquivo  

![image](https://user-images.githubusercontent.com/57811501/121285755-452aca00-c8b5-11eb-846f-8c1cec3e348f.png)

Implementação do **fromByteArray()** para transformar os dados lidos de um vetor de bytes em objeto.

![image](https://user-images.githubusercontent.com/57811501/121286853-d0f12600-c8b6-11eb-9f1c-fb5f0e1c66b7.png)



#### Classe ExtensibleHash

Responsável por toda a parte complexa do código implementação de cada caso de uso:

- **Inserção de um novo elemento:** ![image](https://user-images.githubusercontent.com/57811501/121287495-064a4380-c8b8-11eb-918f-ad50409c55f6.png)


  Se o **bucket não estiver cheio** ele insere o novo objeto no bucket convertendo o objeto em um vetor de bytes para ser inserido no arquivo. **Caso contrário** ele identifica que o bucket está cheio e inicia a duplicação do diretório e cria os novos cestos e insere novamente os elementos nos buckets de acordo com suas respectivas chaves de chaves. 
  ![image](https://user-images.githubusercontent.com/57811501/121288084-2deddb80-c8b9-11eb-8244-a5ca75bf8154.png)

- **Leitura de um elemento no arquivo de prontuários:**
  Essa leitura recebe uma key que é o CPF que é utilizado como índice para encontrar os dados nos buckets, assim que chamada é carregado o diretório atual e acionado o hash do diretório com a key do CPF para retornar índice para o cesto. Logo a frente é utilizado o index para pegar o endereço do cesto buscar esse elemento dentro desse cesto. E retorna esse elemento lido no cesto.
  ![image](https://user-images.githubusercontent.com/57811501/121288641-106d4180-c8ba-11eb-8e68-44419bdd45e9.png)

- **Deletar um elemento da lista de prontuários:**
  Para excluir um elemento a função recebe uma key que é o CPF, carrega o diretório atual, aciona o hash do diretório com a key do CPF para retornar um índice para o cesto. Logo a frente é utilizado o index para encontrar o endereço do cesto, logo após ele carrega o cesto com esse endereço, e faz a exclusão desse cesto, e atualiza o cesto no arquivo.
  ![image](https://user-images.githubusercontent.com/57811501/121290004-44e1fd00-c8bc-11eb-877a-6d0bdbd0faf4.png)

- Para imprimir todos os dados desse arquivo na tela é feito o mesmo procedimento de carregamento de diretório, inicializando a leitura do arquivo do cesto na posição 0, e seguindo a leitura até o fim do arquivo. Imprimindo em um formato legível para o usuário. 
  ![image](https://user-images.githubusercontent.com/57811501/121290605-4e1f9980-c8bd-11eb-8ddc-e05b1c3f81a8.png)



