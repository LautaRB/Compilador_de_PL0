package compilador;

public class Constantes {
    public static int CANT_MAX_IDENT = 1000;
    public static int TOPE_MEMORIA_INICIAL = 1792;
    public static int TOPE_MEMORIA_MAX = 8192;    
    public static int TAMAÑO_HEADER = 512;
    
    ////////// PL0 OPERATION CODES //////////
    public static int EDI_OPCODE = 0xBF; // MOV EDI, ...
    public static int POP_EAX_OPCODE = 0x58;
    public static int POP_EBX_OPCODE = 0x5B;
    public static int MOV_VAR_EAX_OPCODE = 0x89; // MOV [EDI + ...], EAX
    public static int MOV_VAR_EAX_OPCODE2 = 0x87; // SEGUNDA PARTE DE LA INSTRUCCION DE ARRIBA
    public static int CALL_OPCODE = 0xE8; // CALL dir
    public static int JMP_OPCODE = 0xE9; // JUMP INCONDITIONAL
    public static int RET_OPCODE = 0xC3; // RETURN
    public static int JPO_OPCODE = 0x7B; // JUMP IF ODD
    public static int JE_OPCODE = 0x74; // JUMP IF EQUAL
    public static int JNE_OPCODE = 0x75; // JUMP IF NOT EQUAL
    public static int JP_OPCODE = 0x7A; // JUMP IF EVEN
    public static int JL_OPCODE = 0x7C; // JUMP IF LOWER
    public static int JLE_OPCODE = 0x7E; // JUMP IF LOWER OR EQUAL
    public static int JG_OPCODE = 0x7F; // JUMP IF GREATER
    public static int JGE_OPCODE = 0x7D; // JUMP IF GREATER OR EQUAL
    public static int ADD_OPCODE = 0x01; // ADD EAX, EBX
    public static int ADD_OPCODE2 = 0xD8; // SEGUNDA PARTE DE LA INSTRUCCION DE ARRIBA
    public static int SUB_OPCODE = 0x29; // SUB EAX, EBX
    public static int SUB_OPCODE2 = 0xD8; // SEGUNDA PARTE DE LA INSTRUCCION DE ARRIBA
    public static int NEG_OPCODE = 0xF7; // NEG EAX
    public static int NEG_OPCODE2 = 0xD8; // SEGUNDA PARTE DE LA INSTRUCCION DE ARRIBA
    public static int XCHG_OPCODE = 0x93; // XCHG EAX, EBX
    public static int IMUL_OPCODE = 0xF7; // IMUL EBX
    public static int IMUL_OPCODE2 = 0xEB; // SEGUNDA PARTE DE LA INSTRUCCION DE ARRIBA
    public static int IDIV_OPCODE = 0xF7; // IDIV EBX
    public static int IDIV_OPCODE2 = 0xFB; // SEGUNDA PARTE DE LA INSTRUCCION DE ARRIBA
    public static int CDQ_OPCODE = 0x99; // CONVERT DOUBLEWORD TO QUADWORD
    public static int CMP_OPCODE = 0x39; //CMP EBX, EAX
    public static int CMP_OPCODE2 = 0xC3; // SEGUNDA PARTE DE LA INSTRUCCION DE ARRIBA
    public static int PUSH_EAX_OPCODE = 0x50; 
    public static int MOV_EAX_VAR_OPCODE = 0x8B; //MOV EAX, [EDI + ...]
    public static int MOV_EAX_CONST_OPCODE = 0xB8; // MOV EAX, ...
}
