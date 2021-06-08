/*
TABELA HASH EXTENSÍVEL

Os nomes dos métodos foram mantidos em inglês
apenas para manter a coerência com o resto da
disciplina:
- boolean create(T elemento)
- long read(int hashcode)
- boolean update(T novoElemento)   //  a chave deve ser a mesma
- boolean delete(int hashcode)

Implementado pelo Prof. Marcos Kutova
v1.1 - 2021
*/
package aed3;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

