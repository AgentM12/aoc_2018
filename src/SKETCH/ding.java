private static class Program {


	static void execute() {
		// #ip 4

		int r0 = 0, r1 = 0, r2 = 0, r3 = 0, r4 = 0, r5 = 0;

		L5: do {
			r2 = r3 | 65536;         // 6: bori 3 65536 2
			r3 = 1099159;            // 7: seti 1099159 8 3

			L7: do {
				r1 = r2 & 255;           // 8: bani 2 255 1
				r3 += r1;                // 9: addr 3 1 3
				r3 &= 16777215;          // 10: bani 3 16777215 3
				r3 *= 65899;             // 11: muli 3 65899 3
				r3 &= 16777215;          // 12: bani 3 16777215 3
			
				if (r2 < 256) { // 13: gtir 256 2 1
				    break L7;             // 16: seti 27 6 4
				} else {
					r1 = 0;                  // 17: seti 0 8 1
			
					L17: do {
						r5 = r1 + 1;             // 18: addi 1 1 5
						r5 *= 256;               // 19: muli 5 256 5
			
						if (r5 > r2) {           // 20: gtrr 5 2 5
							r2 = r1;                 // 26: setr 1 2 2
							continue L7;
						}
						r1++;                    // 24: addi 1 1 1
					} while(true);
				}
			} while(true);
			L27:
		} while (r3 != r0); // 28: eqrr 3 0 1
	}
}