# Testes do código

Descrição de 4 cenários de teste, **inserção**, **busca**, **exclusão**, e **impressão**.

#### Inserção de 2 elementos: 

Nome: Ana Maria Maia;
Data de Nascimento: 10-05-1964;
Sexo: Feminino;
CPF: 16028347897;
ID: 1;
Resultado:
![image](https://user-images.githubusercontent.com/57811501/121294112-3519e700-c8c3-11eb-8959-d4f859649cad.png)    

Nome: Luís Brandão Teixeira; 
Data de Nascimento: 10-12-1997;
Sexo: Masculino;
CPF: 16028347898;
ID: 2;
Resultado:
   ![image](https://user-images.githubusercontent.com/57811501/121294417-bec9b480-c8c3-11eb-8f89-11119a99cbf6.png)

Podemos notar que logo que o prontuário foi inserido ainda sobravam 2 espaços no bucket de endereço 0, logo após a segunda inserção sobrou somente o 1 porque foi definido em código que o tamanho de cada bucket é de 3 registros.

#### Inserção até a duplicação de diretório: 

Nome: Maria Teresa Maia;
Data de Nascimento: 20-07-1998;
Sexo: Feminino;
CPF: 16028347899;
ID: 3;																				
Resultado:
![image](https://user-images.githubusercontent.com/57811501/121295043-c2117000-c8c4-11eb-8e87-1c0136160af8.png)                     

Nome: Lanaro Brandão Teixeira; 
Data de Nascimento: 14-08-1993;
Sexo: Masculino;
CPF: 16028347900;
ID: 4;
Resultado:
   ![image](https://user-images.githubusercontent.com/57811501/121295218-0a309280-c8c5-11eb-9d46-5c2ac1310269.png)          

Podemos notar que na terceira inserção de prontuário os 3 cestos ficam lotados, e a profundidade global não é alterada. Logo após a inserção do quarto prontuário podemos ver a diferença: O diretório foi duplicado com profundidade global 1, e foi criado outro bucket e também foi redirecionado para cada bucket os prontuários utilizando a chave CPF.



#### Exclusão de elemento:

O prontuário que será  excluído será por CPF
Será o primeiro que foi inserido:
16028347897 da Ana Maria Maia:
![image](https://user-images.githubusercontent.com/57811501/121296325-d6566c80-c8c6-11eb-8396-c023b1b34843.png)

Pode-se notar que a exclusão foi feita corretamente e continua com 2 buckets e o primeiro com apenas 1 prontuário.

#### Inserção de prontuário já cadastrado:

Nome: Maria Teresa Maia; 
Data de nascimento: 20-07-1998;
Sexo: Feminino;
CPF: 16028347899;
ID: 4;
![image](https://user-images.githubusercontent.com/57811501/121297048-19fda600-c8c8-11eb-96ff-0a123f0d7c49.png)

#### Impressão de elementos:

Veremos agora a impressão dos prontuários:
![image](https://user-images.githubusercontent.com/57811501/121297471-b9229d80-c8c8-11eb-9217-55e4fe834bc8.png)