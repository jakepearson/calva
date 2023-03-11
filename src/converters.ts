import * as vscode from 'vscode';
import { jsify } from 'shadow-cljs/calva.js_utils';
import { convert_js_to_cljs } from 'shadow-cljs/calva.js2cljs.converter';
import { convert_dart_to_cljs } from 'shadow-cljs/calva.dartclojure';

type ConverterResult = {
  result: string;
};

type ConverterException = {
  name: string;
  message: string;
};

type JS2CljsError = {
  exception: ConverterException;
  message: string;
  'number-of-parsed-lines': number;
};

type DartClojureError = {
  exception: ConverterException;
  message: string;
};

type ConverterInvalidResult = {
  error: JS2CljsError | DartClojureError;
};

const isConverterResult = (input: any): input is ConverterResult => input.result !== undefined;

type ConvertFn = (code: string) => ConverterResult | ConverterInvalidResult;

async function convert(convertFn: ConvertFn) {
  const editor = vscode.window.activeTextEditor;
  const selection = editor.selection;
  const doc = editor.document;
  const code = doc.getText(
    selection.active.isEqual(selection.anchor)
      ? new vscode.Range(doc.positionAt(0), doc.positionAt(Infinity))
      : new vscode.Range(selection.start, selection.end)
  );
  const results: ConverterResult | ConverterInvalidResult = jsify(convertFn(code));
  if (isConverterResult(results)) {
    await vscode.workspace
      .openTextDocument({ language: 'clojure', content: results.result })
      .then(async (doc) => {
        await vscode.window.showTextDocument(doc, {
          preview: false,
          preserveFocus: false,
        });
      });
  } else {
    void vscode.window.showErrorMessage(results.error.message, {
      modal: true,
      detail: `${results.error.exception.name}: ${results.error.exception.message}`,
    });
  }
}

export async function js2cljs() {
  return convert(convert_js_to_cljs);
}

export async function dart2clj() {
  return convert(convert_dart_to_cljs);
}
