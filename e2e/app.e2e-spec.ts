import {ScheldePage} from './app.po';

describe('schelde App', () => {
  let page: ScheldePage;

  beforeEach(() => {
    page = new ScheldePage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
