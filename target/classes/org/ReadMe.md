AUTORES: Bernardo Muller, David Campos e Francisco Lisboa


Como executar o programa:


Para executar programa, rode o método main() presente no arquivo Main.Java


Caso queira alterar alguma propriedade, vá para o arquivo FileStarter.yml:

rndnumbersPerSeed -> parametro que define quantas vezes será chamado o gerador (por default sempre 100 mil);
seed -> parametro que define a seed (seeds iguais possuem resultados iguais);
rndnumbers -> parametro opcional que serve como numeros aleatórios que serão escolhidos em sequência, em vez de usar o gerador
(somente usado caso não haja parametro seed);

OBS: Caso não houver uma seed nem rndnumbers, a cada execução sera gerada uma seed aleatória;

arrivals -> parametro que define a chegada em qual tempo em qual fila;

queues -> lista de filas, cada fila obrigatóriamente possui servidores, minservice e maxservice, podendo possuir capacidade (caso queira uma fila infinita, basta retirar esse parametro), minArrival e maxArrival;

network -> determina quais filas estão interligadas, sendo necessário passar origem, destino e probabilidade de tal movimentação.
