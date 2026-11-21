package br.com.cantoearte.petshop;

import android.app.*;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.text.InputType;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private final int verde = Color.rgb(32, 125, 83);
    private final int verdeClaro = Color.rgb(232, 245, 237);
    private LocalDb db;
    private LinearLayout root, conteudo;
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        db = new LocalDb(this);
        montarTela();
        mostrarInicio();
    }
    private void montarTela() {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Color.rgb(247,250,247));
        TextView topo = new TextView(this); topo.setText("🐾  CANTO E ARTE\n       Gestão Pet Shop"); topo.setTextColor(Color.WHITE); topo.setTextSize(23); topo.setTypeface(null,Typeface.BOLD); topo.setPadding(20,22,16,20); topo.setBackgroundColor(verde);
        root.addView(topo);
        ScrollView scroll = new ScrollView(this); conteudo = new LinearLayout(this); conteudo.setOrientation(LinearLayout.VERTICAL); conteudo.setPadding(16,14,16,24); scroll.addView(conteudo); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
    }
    private TextView texto(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); if(bold)t.setTypeface(null,Typeface.BOLD); t.setPadding(4,8,4,8); return t;
    }
    private void limpar(String titulo) { conteudo.removeAllViews(); conteudo.addView(texto(titulo,22,verde,true)); }
    private void botao(String label, Runnable acao) {
        Button b = new Button(this); b.setText(label); b.setAllCaps(false); b.setTextColor(verde); b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(verdeClaro));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,5,0,5); conteudo.addView(b,p); b.setOnClickListener(v -> acao.run());
    }
    private void card(String label, String valor) {
        TextView t = texto(label+"\n"+valor,17,verde,true); t.setBackgroundColor(verdeClaro); t.setPadding(16,14,16,14);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,5,0,10); conteudo.addView(t,p);
    }
    private void mostrarInicio() {
        limpar("Painel principal");
        card("Clientes cadastrados",String.valueOf(db.contar("Cliente")));
        card("Pets cadastrados",String.valueOf(db.contar("Pet")));
        card("Agendamentos",String.valueOf(db.contar("Agendamento")));
        card("Produtos no cadastro",String.valueOf(db.contar("Produto")));
        card("Vendas registradas",moeda.format(db.totalVendas()));
        botao("＋ Novo cliente",()->formulario("Cliente"));
        botao("＋ Cadastrar pet",()->formulario("Pet"));
        botao("＋ Agendar banho e tosa",()->formulario("Agendamento"));
        botao("＋ Cadastrar produto",()->formulario("Produto"));
        botao("＋ Cadastrar serviço / preço",()->formulario("Serviço"));
        botao("＋ Registrar venda",()->formulario("Venda"));
        botao("Ver clientes",()->listar("Cliente"));
        botao("Ver pets",()->listar("Pet"));
        botao("Ver agenda",()->listar("Agendamento"));
        botao("Ver estoque / produtos",()->listar("Produto"));
        botao("Ver serviços e preços",()->listar("Serviço"));
        botao("Ver vendas / caixa",()->listar("Venda"));
        conteudo.addView(texto("Seus dados ficam salvos somente neste celular. Faça backup regularmente para evitar perdas.",13,Color.DKGRAY,false));
    }
    private void formulario(String tipo) {
        LinearLayout box = new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(8,4,8,4);
        EditText titulo = campo(tipo.equals("Cliente")?"Nome do cliente": tipo.equals("Pet")?"Nome do pet": tipo.equals("Produto")?"Nome do produto":tipo.equals("Serviço")?"Nome do serviço":tipo.equals("Venda")?"Descrição da venda":"Cliente / pet e serviço");
        EditText detalhe = campo(tipo.equals("Cliente")?"Telefone e observações":tipo.equals("Pet")?"Espécie, raça, idade e nome do tutor":tipo.equals("Produto")?"Quantidade em estoque e observações":tipo.equals("Serviço")?"Descrição do serviço":tipo.equals("Venda")?"Forma de pagamento / observações":"Data, horário, telefone e observações");
        EditText valor = campo("Valor em R$ (se aplicável)"); valor.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        box.addView(titulo); box.addView(detalhe); box.addView(valor);
        new AlertDialog.Builder(this).setTitle("Novo cadastro: "+tipo).setView(box).setNegativeButton("Cancelar",null).setPositiveButton("Salvar",(d,w)->{
            String n=titulo.getText().toString().trim(); if(n.isEmpty()){toast("Informe um nome ou descrição.");return;}
            double v=0; try { v=Double.parseDouble(valor.getText().toString().replace(",", ".")); } catch(Exception ignored){}
            db.inserir(tipo,n,detalhe.getText().toString().trim(),v); toast("Cadastro salvo!"); mostrarInicio();
        }).show();
    }
    private EditText campo(String hint) { EditText e=new EditText(this); e.setHint(hint); e.setSingleLine(false); return e; }
    private void listar(String tipo) {
        limpar(tipo.equals("Agendamento")?"Agenda":tipo.equals("Venda")?"Caixa / vendas":tipo+"s cadastrados");
        ArrayList<String[]> dados=db.listar(tipo);
        if(dados.isEmpty()) conteudo.addView(texto("Nenhum registro cadastrado ainda.",15,Color.DKGRAY,false));
        for(String[] r:dados) {
            LinearLayout item=new LinearLayout(this); item.setOrientation(LinearLayout.VERTICAL); item.setPadding(12,8,12,8); item.setBackgroundColor(Color.WHITE);
            item.addView(texto(r[1],17,verde,true));
            item.addView(texto((r[2]==null?"":r[2])+"\nValor: "+moeda.format(parse(r[3])),14,Color.DKGRAY,false));
            LinearLayout actions=new LinearLayout(this); actions.setGravity(Gravity.END);
            if(tipo.equals("Produto")||tipo.equals("Serviço")) { Button editar=new Button(this); editar.setText("Alterar preço"); editar.setAllCaps(false); editar.setOnClickListener(v->editarValor(r[0],r[1],r[3],tipo)); actions.addView(editar); }
            Button excluir=new Button(this); excluir.setText("Excluir"); excluir.setAllCaps(false); excluir.setOnClickListener(v->new AlertDialog.Builder(this).setMessage("Excluir este registro?").setNegativeButton("Não",null).setPositiveButton("Sim",(a,b)->{db.excluir(Long.parseLong(r[0]));listar(tipo);}).show()); actions.addView(excluir);
            item.addView(actions); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,5,0,8);conteudo.addView(item,p);
        }
        botao("＋ Adicionar",()->formulario(tipo)); botao("Voltar ao painel",()->mostrarInicio());
    }
    private double parse(String s){try{return Double.parseDouble(s);}catch(Exception e){return 0;}}
    private void editarValor(String id,String nome,String atual,String tipo){
        EditText e=campo("Novo valor em R$");e.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);e.setText(atual);
        new AlertDialog.Builder(this).setTitle("Preço: "+nome).setView(e).setNegativeButton("Cancelar",null).setPositiveButton("Salvar",(d,w)->{try{db.atualizarValor(Long.parseLong(id),Double.parseDouble(e.getText().toString().replace(",",".")));listar(tipo);}catch(Exception ex){toast("Valor inválido.");}}).show();
    }
    private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
}
