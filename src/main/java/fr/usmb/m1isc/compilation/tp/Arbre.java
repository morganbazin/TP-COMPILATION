package fr.usmb.m1isc.compilation.tp;
import java.util.ArrayList;

import static fr.usmb.m1isc.compilation.tp.TypeNode.*;

import static java.util.Objects.isNull;

public class Arbre {
    private TypeNode type;
    private String racine;
    private Arbre fg, fd;

    public Arbre(TypeNode type, String racine, Arbre fg, Arbre fd) {
        this.type = type;
        this.racine = racine;
        this.fg = fg;
        this.fd = fd;
    }

    public Arbre(TypeNode type, String racine) {
        this.type = type;
        this.racine = racine;
    }

    public Arbre() {
    }

    public String getRacine() {
        return this.racine;
    }

    public String toString() {
        String resultat = "";
        if ((this.type == OPERATEUR) || (this.type == LET)) {
            resultat += "(" + this.racine;
        } else {
            resultat += " " + this.racine;
        }
        if (!isNull(this.fg)) {
            resultat += this.fg.toString();
        }
        if (!isNull(this.fd)) {
            resultat += this.fd.toString();
        }
        if ((this.type == OPERATEUR) || (this.type == LET)) {
            resultat += ")";
        }
        return resultat;
    }

    public void genData(ArrayList<String> listData) {
        if (this.fg != null) {
            this.fg.genData(listData);
        }
        if (this.fd != null) {
            this.fd.genData(listData);
        }
        if (this.type == IDENT && !listData.contains(this.racine)) {
            listData.add(this.racine);
        }
    }

    public String genCode() {
        StringBuilder resultat = new StringBuilder();

        if (this.type == SEMI) {
            return this.fg.genCode() + this.fd.genCode();
        }

        if (this.type == ENTIER || this.type == IDENT) {
            resultat.append("\tmov eax, ").append(this.racine).append("\n");
        } else if (this.type == OPERATEUR) {
            resultat.append(this.fg.genCode());
            resultat.append("\tpush eax\n");
            resultat.append(this.fd.genCode());
            resultat.append("\tpop ebx\n");
            switch (this.racine) {
                case "+":
                    resultat.append("\tadd eax, ebx\n");
                    break;
                case "-":
                    resultat.append("\tsub ebx, eax\n");
                    resultat.append("\tmov eax, ebx\n");
                    break;
                case "*":
                    resultat.append("\tmul eax, ebx\n");
                    break;
                case "/":
                    resultat.append("\tdiv ebx, eax\n");
                    resultat.append("\tmov eax, ebx\n");
                    break;
                default:
                    break;
            }
        } else if (this.type == LET) {
            resultat.append(this.fd.genCode());
            resultat.append("\tmov ").append(this.fg.racine).append(", eax\n");
        } else if (this.type == INPUT) {
            resultat.append("\tin eax\n");
        } else if (this.type == OUTPUT) {
            resultat.append("\tmov eax, ").append(this.racine).append("\n");
            resultat.append("\tout eax\n");
        }else if (this.type == LT) {
            resultat.append(this.fg.genCode());
            resultat.append( "\tpush eax\n");
            resultat.append( this.fd.genCode());
            resultat.append( "\tpop ebx\n");
            resultat.append( "\tsub eax, ebx\n");
            resultat.append( "\tjle faux_lt_1\n");
            resultat.append( "\tmov eax, 1\n");
            resultat.append( "\tjmp sortie_lt_1\n");
            resultat.append( "faux_lt_1 :\n");
            resultat.append( "\tmov eax, 0\n");
            resultat.append( "sortie_lt_1 :\n");
            return resultat.toString();
        }

        else if (this.type == LTE) {
            resultat.append( this.fg.genCode());
            resultat.append( "\tpush eax\n");
            resultat.append( this.fd.genCode());
            resultat.append( "\tpop ebx\n");
            resultat.append( "\tsub eax, ebx\n");
            resultat.append( "\tjl faux_lte_1\n");
            resultat.append( "\tmov eax, 1\n");
            resultat.append( "\tjmp sortie_lte_1\n");
            resultat.append( "faux_lte_1 :\n");
            resultat.append( "\tmov eax, 0\n");
            resultat.append( "sortie_lte_1 :\n");
            return resultat.toString();
        }
        else if (this.type == GT) {
            resultat.append(this.fg.genCode());
            resultat.append("\tpush eax\n");
            resultat.append(this.fd.genCode());
            resultat.append("\tpop ebx\n");
            resultat.append("\tsub eax, ebx\n");
            resultat.append("\tjle faux_gt_1\n");
            resultat.append("\tmov eax, 1\n");
            resultat.append("\tjmp sortie_gt_1\n");
            resultat.append("faux_gt_1 :\n");
            resultat.append("\tmov eax, 0\n");
            resultat.append("sortie_gt_1 :\n");
            return resultat.toString();
        }

        else if (this.type == GTE) {
            resultat.append(this.fg.genCode());
            resultat.append("\tpush eax\n");
            resultat.append(this.fd.genCode());
            resultat.append("\tpop ebx\n");
            resultat.append("\tsub eax, ebx\n");
            resultat.append("\tjg faux_gte_1\n");
            resultat.append("\tmov eax, 1\n");
            resultat.append("\tjmp sortie_gte_1\n");
            resultat.append("faux_gte_1 :\n");
            resultat.append("\tmov eax, 0\n");
            resultat.append("sortie_gte_1 :\n");
            return resultat.toString();
        }
        else if (this.type == EGAL) {
            resultat.append( this.fg.genCode());
            resultat.append( "\tpush eax\n");
            resultat.append( this.fd.genCode());
            resultat.append( "\tpop ebx\n");
            resultat.append( "\tsub eax, ebx\n");
            resultat.append( "\tjnz faux_egal_1\n");
            resultat.append( "\tmov eax, 1\n");
            resultat.append( "\tjmp sortie_egal_1\n");
            resultat.append( "faux_egal_1 :\n");
            resultat.append( "\tmov eax, 0\n");
            resultat.append( "sortie_egal_1 :\n");
            return resultat.toString();
        }
        else if (this.type == MOD) {
            resultat.append( this.fd.genCode());
            resultat.append( "\tpush eax\n");
            resultat.append( this.fg.genCode());
            resultat.append( "\tpop ebx\n");
            resultat.append( "\tmov ecx, eax\n");
            resultat.append( "\tdiv ecx, ebx\n");
            resultat.append( "\tmul ecx, ebx\n");
            resultat.append( "\tsub eax, ecx\n");
            return resultat.toString();
        }
        else if(this.type == WHILE){
            resultat.append( "debut_while_1:\n");
            resultat.append( this.fg.genCode());
            resultat.append( "\tjz sortie_while_1\n");
            resultat.append( this.fd.genCode());
            resultat.append( "\tjmp debut_while_1\n");
            resultat.append( "sortie_while_1:\n");
            return resultat.toString();
        }

        else if (this.type == IF) {
            resultat.append( this.fg.genCode());
            resultat.append( "\tjz faux_if_1\n");
            resultat.append( this.fd.fg.genCode());
            resultat.append( "\tjmp sortie_if_1\n");
            resultat.append( "faux_if_1 :\n");
            resultat.append( this.fd.fd.genCode());
            resultat.append( "sortie_if_1 :\n");
            return resultat.toString();
        }

        else if (this.type == AND) {
            resultat.append( this.fg.genCode());
            resultat.append( "\tjz faux_and_1\n");
            resultat.append( this.fd.genCode());
            resultat.append( "faux_and_1 :\n");
            return resultat.toString();
        }


        else if (this.type == OR) {
            resultat.append( this.fg.genCode());
            resultat.append( "\tjnz vrai_or_1\n");
            resultat.append( this.fd.genCode());
            resultat.append( "\tjnz vrai_or_1\n");
            resultat.append( "vrai_or_1 :\n");
            return resultat.toString();
        }

        else if (this.type == NOT) {
            resultat.append( this.fg.genCode());
            resultat.append( "\tjnz faux_not_1\n");
            resultat.append( "\tmov eax, 1\n");
            resultat.append( "\tjmp sortie_not_1\n");
            resultat.append( "faux_not_1 :\n");
            resultat.append( "\tmov eax, 0\n");
            resultat.append( "sortie_not_1 :\n");
            return resultat.toString();
        }

        return resultat.toString();
    }

    public String generation() {
        StringBuilder resultat = new StringBuilder();
        resultat.append("DATA SEGMENT\n");
        ArrayList<String> listData = new ArrayList<>();
        this.genData(listData);
        for(String data : listData){
            resultat.append("\t ").append(data).append(" DD\n");
        }
        resultat.append("DATA ENDS\n");
        resultat.append("CODE SEGMENT\n");
        resultat.append(this.genCode());
        resultat.append("CODE ENDS");
        return resultat.toString();
    }



}
