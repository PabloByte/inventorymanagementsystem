class Category {


    #id;
    
    #name;
    
    #description;
    #products;


    constructor(  id, name, description, products = []) {
        this.#id = id;
        this.#name = name;
        this.#description = description;
       this.#products = products || [];

       
    }

    getId() {
        return this.#id;
    }   
    getName() {
        return this.#name;
    }
    getDescription() {
        return this.#description;
    }
 
    setId(id) {
        this.#id = id;
    }
    setName(name) {
        this.#name = name;
    }
    setDescription(description) {
        this.#description = description;
    }
    setProducts(products) {
        this.#products = products;
    }
    getProducts() {
        return this.#products;
    }
 
    toString() {
    return `Category [id=${this.#id}, name=${this.#name}, description=${this.#description}, products=${this.#products}]`;
}


    toJSON() {
        return {
            id: this.#id,
            name: this.#name,
            description: this.#description,
            products: this.#products // Uncomment if you want to include products in the JSON representation
            
        };
    }
    
   
 








    
}