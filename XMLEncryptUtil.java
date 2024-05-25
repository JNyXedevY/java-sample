import org.w3c.dom.Document;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureClassLoader;
import java.util.Collections;

public class Main {
  public static void main(String[] args) throws Exception {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    KeyPair kp = kpg.generateKeyPair();

    XMLSignatureFactory xmlSf = XMLSignatureFactory.getInstance("DOM");
    KeyInfoFactory kif = xmlSf.getKeyInfoFactory();
    KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kif.newKeyValue(kp.getPublic())));

    CanonicalizationMethod clm = xmlSf.newCanonicalizationMethod(
        CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null
    );
    Reference ref = xmlSf.newReference("", xmlSf.newDigestMethod(DigestMethod.SHA3_512, null));
    SignedInfo si = xmlSf.newSignedInfo(
        clm,
        xmlSf.newSignatureMethod(SignatureMethod.RSA_SHA512, null),
        Collections.singletonList(ref)
    );
    XMLSignature xmlSignature = xmlSf.newXMLSignature(si, ki);

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newDefaultNSInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document doc = db.parse(SecureClassLoader.getSystemResourceAsStream("sample.xml"));

    xmlSignature.sign(new DOMSignContext(kp.getPrivate(), doc.getDocumentElement()));
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer trans = tf.newTransformer();
    trans.transform(new DOMSource(doc), new StreamResult(new File("signed.xml")));
  }
}

