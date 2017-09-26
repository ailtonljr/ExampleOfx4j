package br.eng.ailton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import com.webcohesion.ofx4j.domain.data.MessageSetType;
import com.webcohesion.ofx4j.domain.data.ResponseEnvelope;
import com.webcohesion.ofx4j.domain.data.ResponseMessage;
import com.webcohesion.ofx4j.domain.data.ResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.banking.BankStatementResponseTransaction;
import com.webcohesion.ofx4j.domain.data.banking.BankingResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.common.Transaction;
import com.webcohesion.ofx4j.domain.data.creditcard.CreditCardResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.creditcard.CreditCardStatementResponseTransaction;
import com.webcohesion.ofx4j.domain.data.signon.SignonResponse;
import com.webcohesion.ofx4j.io.AggregateMarshaller;
import com.webcohesion.ofx4j.io.AggregateUnmarshaller;
import com.webcohesion.ofx4j.io.OFXHandler;
import com.webcohesion.ofx4j.io.OFXReader;
import com.webcohesion.ofx4j.io.nanoxml.NanoXMLOFXReader;
import com.webcohesion.ofx4j.io.v1.OFXV1Writer;
import com.webcohesion.ofx4j.io.v2.OFXV2Writer;

import net.n3.nanoxml.*; //1

public class FixCCBBOfx {

	public static void main(String[] args) throws Exception {

		// AggregateUnmarshaller<ResponseEnvelope> a = new
		// AggregateUnmarshaller<ResponseEnvelope>(ResponseEnvelope.class);
		File ofxFile = new File(System.getProperty("user.home") + "\\eclipse-workspace\\FixBBCCOfx\\teste.ofx");

		AggregateUnmarshaller<ResponseEnvelope> unmarshaller = new AggregateUnmarshaller<ResponseEnvelope>(
				ResponseEnvelope.class);

		ResponseEnvelope re = unmarshaller.unmarshal(new FileInputStream(ofxFile));

		SignonResponse sr = re.getSignonResponse();

		MessageSetType type = MessageSetType.creditcard;
		ResponseMessageSet message = re.getMessageSet(type);

		MessageSetType messageType = message.getType();

		((CreditCardResponseMessageSet) message).getStatementResponses().get(0).getMessage().getTransactionList()
				.getTransactions().get(0).setAmount(-123.45);

		if (message != null) {
			List<CreditCardStatementResponseTransaction> bank = ((CreditCardResponseMessageSet) message)
					.getStatementResponses();
			for (CreditCardStatementResponseTransaction b : bank) {
				System.out.println("cc: " + b.getMessage().getAccount().getAccountNumber());
				// System.out.println("ag: " + b.getMessage().getAccount().getBranchId());
				System.out.println("balanço final: " + b.getMessage().getLedgerBalance().getAmount());
				System.out.println("dataDoArquivo: " + b.getMessage().getLedgerBalance().getAsOfDate());
				List<Transaction> list = b.getMessage().getTransactionList().getTransactions();
				System.out.println("TRANSAÇÕES\n");
				for (Transaction transaction : list) {
					System.out.println("tipo: " + transaction.getTransactionType().name());
					System.out.println("id: " + transaction.getId());
					System.out.println("data: " + transaction.getDatePosted());
					System.out.println("valor: " + transaction.getAmount());
					System.out.println("descricao: " + transaction.getMemo());
					System.out.println("");
				}
			}
		}

		StringWriter marshalled = new StringWriter();
		OFXV1Writer writer = new OFXV1Writer(marshalled);
		new AggregateMarshaller().marshal(re, writer);
		writer.close();

		System.out.println(marshalled.toString());

		try (PrintWriter out = new PrintWriter("saved.ofx")) {
			out.println(marshalled.toString());
		}

	}

}