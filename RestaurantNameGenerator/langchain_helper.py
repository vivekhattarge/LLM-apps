#from langchain_openai import ChatOpenAI
from langchain.prompts import PromptTemplate
from langchain.chains import LLMChain
from langchain.chains import SequentialChain
from langchain_community.llms import GPTNeo
from secret_key import openapi_key
import os

os.environ['OPENAI_API_KEY'] = openapi_key

#llm = ChatOpenAI(temperature=0.7)

llm = GPTNeo.from_pretrained('EleutherAI/gpt-neo-2.7B')
#chain = LLMChain(llm=model)


def getRestaurantNameAndItems(cuisine):
    # Chain 1: Restaurant Name
    prompt_template_name = PromptTemplate(
        input_variables=['cuisine'],
        template=f"I want to open a restaurant for {cuisine} food. Suggest a fancy name for this."
    )

    name_chain = LLMChain(llm=llm, promt=prompt_template_name, output_key="restaurant_name")

    # Chain 2: Menu Items
    prompt_template_items = PromptTemplate(
        input_variables=['restaurant_name'],
        template="""suggest some menu items for {restaurant_name}. Return it as a comma separated string"""
    )

    food_items_chain = LLMChain(llm=llm, promt=prompt_template_items, output_key="menu_items")

    chain = SequentialChain(
        chains=[name_chain, food_items_chain],
        input_variables=['cuisine'],
        output_variables=['restaurant_name', 'menu_items']
    )
    response = chain({'cuisine': cuisine})
    return response


if __name__ == "__main__":
    print(getRestaurantNameAndItems("Italian"))
