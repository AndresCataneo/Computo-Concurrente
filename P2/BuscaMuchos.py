from collections import Counter
import re
import sys

def contar_numeros(archivo):
    contador = Counter()

    # errors="ignore" evita problemas de codificación
    with open(archivo, "r", encoding="utf-16", errors="ignore") as f:
        for linea in f:
            linea = linea.strip()

            m = re.search(r"Result:\s*(\d+)", linea)
            if m:
                contador[int(m.group(1))] += 1
                continue

            if re.fullmatch(r"\d+", linea):
                contador[int(linea)] += 1

    return contador


def main():
    archivo = sys.argv[1]
    conteo = contar_numeros(archivo)

    print("Números que aparecen más de 2 veces:")
    for n, c in conteo.items():
        if c > 2:
            print(n, c)

main()