package com.nisira.libgdx.tools;

import java.util.ArrayList;
import java.util.List;


public class Floyd {
	private float[][] D;
	private int[][] S;
	int x;
	// private EscribirArchivos Archi = new EscribirArchivos();
	final int inf = -1;
	
	public Floyd(float[][] D, int[][] S, int x) {
		this.D = D;
		this.S = S;
		this.x = x;
		
	}
	public Floyd() {
		this.x = 5;
		D = new float[x][x];
		S = new int[x][x];

		for (int i = 0; i < x; i++) {
			for (int j = 0; j < x; j++) {
				if (i == j) {
					D[i][j] = -2;
					S[i][j] = -2;
				} else {
					D[i][j] = -1;
					S[i][j] = j;
				}
			}
		}
		D[0][1] = 3;
		D[0][2] = 10;
		D[1][0] = 3;
		D[1][3] = 5;
		D[2][0] = 10;
		D[2][3] = 6;
		D[2][4] = 15;
		D[3][1] = 5;
		D[3][2] = 6;
		D[3][4] = 4;
		D[4][2] = 15;
		D[4][3] = 4;
	}
	/*
	 * public Floyd(ArrayList<float[]> dist, int x) { this.x = x; D = new
	 * float[x][x]; S = new int[x][x]; for (int i = 0; i < x; i++) { for (int j
	 * = 0; j < x; j++) { if (i == j) { D[i][j] = -2; S[i][j] = -2; } else {
	 * D[i][j] = -1; S[i][j] = j; } } } for (int i = 0; i < dist.size(); i++) {
	 * D[(int) dist.get(i)[0]][(int) dist.get(i)[1]] = dist.get(i)[2]; } }
	 */

	public void EscribirMatricez() {
		for (int j = 0; j < x; j++)
			System.out.print("\t[" + j + "]");
		System.out.print("\n");
		for (int i = 0; i < x; i++) {
			System.out.print("[" + i + "]\t");
			for (int j = 0; j < x; j++) {
				System.out.print(D[i][j] + "\t");
			}
			System.out.print("\n");
		}
		for (int j = 0; j < x; j++)
			System.out.print("\t[" + j + "]");
		System.out.print("\n");
		for (int i = 0; i < x; i++) {
			System.out.print("[" + i + "]\t");
			for (int j = 0; j < x; j++) {
				System.out.print(S[i][j] + "\t");
			}
			System.out.print("\n");
		}
	}

	public int[][] retornarD() {
		rutaFloyd();
		return S;
	}

	public void rutaFloyd() {
		for (int k = 0; k < x; k++) {
			for (int i = 0; i < x; i++) {
				if (i != k && D[i][k] != inf) {
					for (int j = 0; j < x; j++) {
						if (j != k && D[k][j] != inf && i != j) {
							if (esMenor(D[i][k] + D[k][j], D[i][j])) {
								D[i][j] = D[i][k] + D[k][j];
								S[i][j] = k;
								try {
									// Archi.guardarFloyd(D, S,x);ww
								} catch (Exception e) {

								}
							}
						}
					}
				}
			}
		}
	}

	private boolean esMenor(float a, float b) {
		if (b != inf) {
			return a < b;
		}
		return true;
	}

	public ArrayList<Integer> solucion(int a, int b) {
		ArrayList<Integer> lista = new ArrayList<Integer>();
		String cad = a + " " + MRuta(a, b);
		System.out.println("SOLUCION: " + cad);
		for (int i = 0; i < cad.length(); i++) {
			if (cad.charAt(i) != ' ') {
				String cn = "";
				while (i < cad.length() && cad.charAt(i) != ' ') {
					cn = cn + cad.charAt(i);
					i++;
				}
				int num = Integer.parseInt(cn);
				lista.add(num);
			}
		}
		return lista;
	}

	public String MRuta(int a, int b) {
		if (S[a][b] == b) {
			return String.valueOf(b);
		} else {
			return (MRuta(a, S[a][b]) + " " + MRuta(S[a][b], b));
		}
	}
	
	public List<Integer> MRutaList(int a, int b) {
		List<Integer> l = new ArrayList<Integer>();
		
		if (S[a][b] == b) {
			l.add(b);
		} else {
			l.addAll(MRutaList(a, S[a][b]));
			l.addAll(MRutaList(S[a][b], b));
			//return (MRutaList(a, S[a][b]) + " " + MRutaList(S[a][b], b));
			//return (MRuta(a, S[a][b]) + " " + MRuta(S[a][b], b));			
		}
		return l;
	}
	
//	public static void main(String[] args) {
//		Floyd f = new Floyd();		
//		f.rutaFloyd();
//		System.out.println(f.MRuta(1, 2)); 
//	}
}