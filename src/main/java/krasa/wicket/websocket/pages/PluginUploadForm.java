package krasa.wicket.websocket.pages;

import java.util.List;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.*;
import org.apache.wicket.util.lang.Bytes;
import org.slf4j.*;

/**
 * @author Vojtech Krasa
 */
public class PluginUploadForm extends Form<Void> {

	private static final Logger log = LoggerFactory.getLogger(PluginUploadForm.class);

	FileUploadField fileUploadField;

	public PluginUploadForm(String id) {
		super(id);
		setMultiPart(true);
		add(fileUploadField = new FileUploadField("fileInput"));
		setMaxSize(Bytes.kilobytes(25000));
	}

	public FileUploadField getFileUploadField() {
		return fileUploadField;
	}

	@Override
	protected void onSubmit() {
		List<FileUpload> uploads = fileUploadField.getFileUploads();
		if (uploads.isEmpty()) {
			throw new RuntimeException("No file uploaded");
		}
		for (FileUpload upload : uploads) {
			try {
				info("uploaded: " + upload.getClientFileName());
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
	}

}
